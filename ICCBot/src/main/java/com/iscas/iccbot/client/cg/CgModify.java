package com.iscas.iccbot.client.cg;

import com.iscas.iccbot.Analyzer;
import com.iscas.iccbot.MyConfig;
import com.iscas.iccbot.SummaryLevel;
import com.iscas.iccbot.analyze.utils.ConstantUtils;
import com.iscas.iccbot.analyze.utils.SootUtils;
import lombok.extern.slf4j.Slf4j;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

import java.util.*;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CgModify extends Analyzer {

    public CgModify() {
        super();
    }

    @Override
    public void analyze() {
        addEdgesByOurAnalyze(appModel.getCg());
        removeExlibEdge(appModel.getCg());
        removeSameEdge(appModel.getCg());
        removeSelfEdge(appModel.getCg());
        if (MyConfig.getInstance().getMySwitch().getSummaryStrategy().equals(SummaryLevel.none)) {
            addTopoForSupplySingle();

        } else if (MyConfig.getInstance().getMySwitch().isCgAnalyzeGroupedStrategy()) {
            // multiple topo queue
            List<CallGraph> cgs = new ArrayList<>();
            List<Set<SootMethod>> methodSets = new ArrayList<>();
            separateCG2multiple(appModel.getCg(), methodSets, cgs);
            for (int i = 0; i < cgs.size(); i++) {
                CallGraph cg = cgs.get(i);
                Set<SootMethod> methodSet = methodSets.get(i);
                Map<SootMethod, Integer> inDegreeMap = constructInDegreeMap(cg, methodSet);
                removeCircleFromCG(inDegreeMap, cg);
                Map<SootMethod, Integer> outDegreeMap = constructOutDegreeMap(cg, methodSet);
                sortCG(outDegreeMap, cg);
            }
            addTopoForSupplyMulti();
        } else {
            // single topo queue
            Map<SootMethod, Integer> inDegreeMap = constructInDegreeMap(appModel.getCg());
            removeCircleFromCG(inDegreeMap, appModel.getCg());
            Map<SootMethod, Integer> outDegreeMap = constructOutDegreeMap(appModel.getCg());
            sortCG(outDegreeMap, appModel.getCg());
            addTopoForSupplySingle();
        }
        log.info("Call Graph has " + appModel.getCg().size() + " edges");
    }

    /**
     * addTopoForSupplyMulti
     */
    private void addTopoForSupplyMulti() {
        log.info("addTopoForSupplyMulti");
        List<SootMethod> subTopo = new ArrayList<SootMethod>();
        appModel.getTopoMethodQueueSet().add(subTopo);
        for (SootClass sc : Scene.v().getApplicationClasses()) {
            if (!MyConfig.getInstance().getMySwitch().allowLibCodeSwitch()) {
                if (!SootUtils.isNonLibClass(sc.getName()))
                    continue;
            }
            for (SootMethod sm : sc.getMethods()) {
                if (!SootUtils.hasSootActiveBody(sm))
                    continue;
                if (!appModel.getTopoMethodQueue().contains(sm)) {
                    appModel.getTopoMethodQueue().add(0, sm);
                    subTopo.add(0, sm);
                }
            }
        }
    }

    /**
     * addTopoForSupplyMulti
     */
    private void addTopoForSupplySingle() {
        log.info("addTopoForSupplySingle");
        if (appModel.getTopoMethodQueueSet().size() == 0)
            appModel.getTopoMethodQueueSet().add(new ArrayList<SootMethod>());
        for (List<SootMethod> subTopo : appModel.getTopoMethodQueueSet()) {
            for (SootClass sc : Scene.v().getApplicationClasses()) {
                if (!MyConfig.getInstance().getMySwitch().allowLibCodeSwitch()) {
                    if (!SootUtils.isNonLibClass(sc.getName()))
                        continue;
                }
                for (SootMethod sm : sc.getMethods()) {
                    if (!SootUtils.hasSootActiveBody(sm))
                        continue;
                    if (!appModel.getTopoMethodQueue().contains(sm)) {
                        appModel.getTopoMethodQueue().add(0, sm);
                        subTopo.add(0, sm);
                    }
                }
            }
            break;
        }
    }

    /**
     * Separate big cg to a set of connected small one
     *
     * @param callGraph
     * @return
     */
    private void separateCG2multiple(CallGraph callGraph, List<Set<SootMethod>> methodSets, List<CallGraph> cgs) {
        for (Edge edge : callGraph) {
            int srcId = -1, tgtId = -1;
            SootMethod srcMtd = edge.getSrc().method();
            SootMethod tgtMtd = edge.getTgt().method();
            for (int i = 0; i < methodSets.size(); i++) {
                if (methodSets.get(i).contains(srcMtd)) {
                    srcId = i;
                    break;
                }
            }
            for (int i = 0; i < methodSets.size(); i++) {
                if (methodSets.get(i).contains(tgtMtd)) {
                    tgtId = i;
                    break;
                }
            }
            if (srcId == -1 && tgtId == -1) {
                Set<SootMethod> newSet = new HashSet<SootMethod>();
                newSet.add(edge.getSrc().method());
                newSet.add(edge.getTgt().method());
                methodSets.add(newSet);
                CallGraph cg = new CallGraph();
                Edge edgeCopy = new Edge(edge.getSrc(), edge.srcStmt(), edge.getTgt(), edge.kind());
                cg.addEdge(edgeCopy);
                cgs.add(cg);
            } else if (srcId != -1 && tgtId == -1) {
                methodSets.get(srcId).add(edge.getTgt().method());
                Edge edgeCopy = new Edge(edge.getSrc(), edge.srcStmt(), edge.getTgt(), edge.kind());
                cgs.get(srcId).addEdge(edgeCopy);
            } else if (srcId == -1) {
                methodSets.get(tgtId).add(edge.getSrc().method());
                Edge edgeCopy = new Edge(edge.getSrc(), edge.srcStmt(), edge.getTgt(), edge.kind());
                cgs.get(tgtId).addEdge(edgeCopy);
            } else {
                Edge edgeCopy = new Edge(edge.getSrc(), edge.srcStmt(), edge.getTgt(), edge.kind());
                cgs.get(srcId).addEdge(edgeCopy);
                if (srcId != tgtId) {
                    for (SootMethod sig : methodSets.get(tgtId))
                        methodSets.get(srcId).add(sig);
                    methodSets.remove(tgtId);
                    for (Edge tarEdge : cgs.get(tgtId)) {
                        Edge targetCopy = new Edge(tarEdge.getSrc(), tarEdge.srcStmt(), tarEdge.getTgt(),
                                tarEdge.kind());
                        cgs.get(srcId).addEdge(targetCopy);
                    }
                    cgs.remove(tgtId);
                }
            }
        }
    }

    /**
     * add edges in cg for topo sort
     */
    private void addEdgesByOurAnalyze(CallGraph callGraph) {
        soot.util.Chain<SootClass> appClasses = Scene.v().getApplicationClasses();

        ThreadPoolExecutor smAnalyzeExecutor = new ThreadPoolExecutor(8, Integer.MAX_VALUE, 0L,
            TimeUnit.MICROSECONDS, new SynchronousQueue<>(), r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            });

        int cnt = 0;
        for (SootClass sc : appClasses) {
            if (!MyConfig.getInstance().getMySwitch().allowLibCodeSwitch() &&
                    !SootUtils.isNonLibClass(sc.getName())) {
                continue;
            }
            cnt += sc.getMethodCount();
        }
        log.info("There are totally {} methods to expand", cnt);

        int totalCnt = cnt;
        new Thread("MonitorThread") {
            @Override
            public void run() {
                long tickTime = System.currentTimeMillis();
                while (!smAnalyzeExecutor.isShutdown()) {
                    if (System.currentTimeMillis() - tickTime > 3000) {
                        log.info("Traversing all SootMethods: {}/{}/{}", smAnalyzeExecutor.getActiveCount(),
                                smAnalyzeExecutor.getCompletedTaskCount(), totalCnt);
                        tickTime = System.currentTimeMillis();
                    }
                    Thread.yield();
                }
            }
        }.start();
        for (SootClass sc : appClasses) {
            if (!MyConfig.getInstance().getMySwitch().allowLibCodeSwitch() &&
                    !SootUtils.isNonLibClass(sc.getName())) {
                continue;
            }
            for (SootMethod sm : sc.getMethods()) {
                smAnalyzeExecutor.submit(() -> {
                    if (!SootUtils.hasSootActiveBody(sm)) return;
                    soot.UnitPatchingChain unitPatchingChain = SootUtils.getSootActiveBody(sm).getUnits();
                    for (Unit u : unitPatchingChain) {
                        InvokeExpr exp = SootUtils.getInvokeExp(u);
                        if (exp == null) continue;
                        InvokeExpr invoke = SootUtils.getSingleInvokedMethod(u);
                        if (invoke != null) { // u is invoke stmt
                            Set<SootMethod> targetSet = SootUtils.getInvokedMethodSet(sm, u);
                            for (SootMethod target : targetSet) {
                                Edge e = new Edge(sm, (Stmt) u, target);
                                callGraph.addEdge(e);
                            }
                        }
                    }
                });
            }
        }
        try {
            smAnalyzeExecutor.shutdown();
            boolean res = smAnalyzeExecutor.awaitTermination(24, TimeUnit.HOURS);
            if (res) {
                log.info("SootMethod traverse finished");
            } else {
                log.info("SootMethod traverse timed out");
            }
        } catch (InterruptedException e) {
            log.error("InterruptedException when executing smAnalyze", e);
        }
    }

    /**
     * if an edge not in package, remove it
     */
    private void removeExlibEdge(CallGraph callGraph) {
        log.info("Remove external lib edges");
        if (!MyConfig.getInstance().getMySwitch().allowLibCodeSwitch()) {
            Set<Edge> toBeDeletedSet = new HashSet<>();
            for (Edge edge : callGraph) {
                if (!SootUtils.isNonLibClass(Objects.requireNonNull(edge.src()).getDeclaringClass().getName()))
                    toBeDeletedSet.add(edge);
                else if (!SootUtils.isNonLibClass(Objects.requireNonNull(edge.tgt()).getDeclaringClass().getName()))
                    toBeDeletedSet.add(edge);
            }
            for (Edge tbEdge : toBeDeletedSet)
                callGraph.removeEdge(tbEdge, false);
        }
    }

    /**
     * remove a -- a
     */
    private void removeSelfEdge(CallGraph callGraph) {
        log.info("Remove self edges");
        Set<Edge> toBeDeletedSet = new HashSet<>();
        for (Edge edge : callGraph) {
            SootMethod srcMethod = edge.src();
            SootMethod tgtMethod = edge.tgt();
            if (srcMethod == null || tgtMethod == null ||
                    srcMethod == tgtMethod) {
                toBeDeletedSet.add(edge);
            }
        }
        for (Edge tbEdge : toBeDeletedSet)
            callGraph.removeEdge(tbEdge, false);
    }

    /**
     * remove same edges
     */
    private void removeSameEdge(CallGraph callGraph) {
        log.info("remove same edges");
        Set<String> edgeSet = new HashSet<>();
        Set<Edge> toBeDeletedSet = new HashSet<>();
        for (Edge edge : callGraph) {
            SootMethod srcMethod = edge.src();
            SootMethod tgtMethod = edge.tgt();
            String sig = srcMethod.getSignature() + tgtMethod.getSignature();
            if (edgeSet.contains(sig))
                toBeDeletedSet.add(edge);
            else
                edgeSet.add(sig);
        }
        for (Edge tbEdge : toBeDeletedSet) {
            callGraph.removeEdge(tbEdge, false);
        }
    }

    /**
     * remove Circle from cg use stack based dfs, fast
     *
     * @param inDegreeMap in degree map
     * @param callGraph call graph
     */
    private void removeCircleFromCG(Map<SootMethod, Integer> inDegreeMap, CallGraph callGraph) {
        log.info("removeCircleFromCG");
        Set<Edge> toBeDeletedSet = new HashSet<Edge>();
        Set<SootMethod> mcSrcs = new HashSet<SootMethod>(inDegreeMap.keySet());
        for (SootMethod mcSrc : mcSrcs) {
            if (inDegreeMap.get(mcSrc) == 0) {// for nodes whose indegree are 0,
                // start from them
                Stack<SootMethod> stack = new Stack<SootMethod>();
                stack.add(mcSrc);
                Map<SootMethod, Integer> nodeStatus = new HashMap<>();
                nodeStatus.put(mcSrc, -1);
                while (!stack.isEmpty()) {
                    SootMethod topMethod = stack.peek();
                    Iterator<Edge> it = callGraph.edgesOutOf(topMethod);
                    boolean allVisited = true;
                    //for all the callees of the peek method
                    while (it.hasNext()) {
                        Edge edge = it.next();
                        SootMethod nextMethod = edge.getTgt().method();
                        if (!nodeStatus.containsKey(nextMethod)) {
                            allVisited = false;
                            nodeStatus.put(nextMethod, -1);
                            stack.push(nextMethod);
                            break;
                        } else if (nodeStatus.get(nextMethod) == -1) {
                            toBeDeletedSet.add(edge);
                        }
                    }
                    if (allVisited) {
                        nodeStatus.put(topMethod, 1);
                        stack.pop();
                    }
                }
            }
            for (Edge tbEdge : toBeDeletedSet)
                callGraph.removeEdge(tbEdge, false);
        }
    }

    /**
     * sort cg and get a sorted node list method that has no active body is
     * excluded
     *
     * @param outDegreeMap
     * @param callGraph
     */
    private void sortCG(Map<SootMethod, Integer> outDegreeMap, CallGraph callGraph) {
        log.info("call graph sorting......");
        if (callGraph == null)
            return;
        Set<SootMethod> queueSet = new HashSet<SootMethod>();
        List<SootMethod> subTopo = new ArrayList<SootMethod>();
        appModel.getTopoMethodQueueSet().add(subTopo);

        // put node whose outdegree is 0 to queue
        int lastTurn = 0;
        long startTime = System.currentTimeMillis();
        long endTime;
        while (true) {
            endTime = System.currentTimeMillis();
            if(((endTime - startTime) / 1000) > ConstantUtils.CGSORTTIME) {
                log.info("incomplete call graph sorting");
                break;
            }
            Set<SootMethod> mcTars = new HashSet<SootMethod>(outDegreeMap.keySet());
            for (SootMethod mcTar : mcTars) {
                if (outDegreeMap.get(mcTar) == 0) {// for nodes whose outdegree
                    // are 0, remove them
                    outDegreeMap.remove(mcTar);
                    if (!queueSet.contains(mcTar)) {
                        queueSet.add(mcTar);
                        subTopo.add(mcTar);
                        appModel.getTopoMethodQueue().add(mcTar);
                        Iterator<Edge> it2 = callGraph.edgesInto(mcTar);
                        while (it2.hasNext()) {// for nodes whose link to
                            // removed nodes, outdegree -1
                            SootMethod mcSource = it2.next().getSrc().method();
                            if (!outDegreeMap.containsKey(mcSource))
                                continue;
                            outDegreeMap.put(mcSource, outDegreeMap.get(mcSource) - 1);
                        }
                    }
                }
            }
            if (outDegreeMap.size() == lastTurn)
                for (SootMethod key : outDegreeMap.keySet())
                    outDegreeMap.put(key, 0);
            lastTurn = outDegreeMap.size();
            if (outDegreeMap.size() == 0)
                break;
        }
    }

    /**
     * constructOutDregreeMap
     *
     * @param callGraph
     * @return
     */
    private Map<SootMethod, Integer> constructOutDegreeMap(CallGraph callGraph) {
        Map<SootMethod, Integer> outDegreeMap = new HashMap<SootMethod, Integer>();// get
        // outDegreeMap
        for (SootClass sc : Scene.v().getApplicationClasses()) {
            for (SootMethod m : sc.getMethods()) {
                if (!SootUtils.hasSootActiveBody(m))
                    continue;
                outDegreeMap.put(m, 0); // initial outDegreeMap
            }
        }
        for (Edge edge : callGraph) {
            if (!SootUtils.hasSootActiveBody(edge.getTgt().method()))
                continue;
            if (outDegreeMap.containsKey(edge.getSrc().method()))
                outDegreeMap.put(edge.getSrc().method(), outDegreeMap.get(edge.src()) + 1);
        }
        return outDegreeMap;
    }

    /**
     * constructInDregreeMap
     *
     * @param callGraph call graph
     * @return
     */
    private Map<SootMethod, Integer> constructInDegreeMap(CallGraph callGraph) {
        Map<SootMethod, Integer> inDegreeMap = new HashMap<SootMethod, Integer>();// get
        // outDegreeMap
        for (SootClass sc : Scene.v().getApplicationClasses()) {
            for (SootMethod m : sc.getMethods()) {
                if (!SootUtils.hasSootActiveBody(m))
                    continue;
                inDegreeMap.put(m, 0); // initial outDegreeMap
            }
        }
        for (Edge edge : callGraph) {
            if (!SootUtils.hasSootActiveBody(edge.getSrc().method()))
                continue;
            if (inDegreeMap.containsKey(edge.getTgt().method()))
                inDegreeMap.put(edge.getTgt().method(), inDegreeMap.get(edge.tgt()) + 1);
        }
        return inDegreeMap;
    }

    /**
     * constructOutDegreeMap
     *
     * @param callGraph call graph
     * @param methodSet method set
     * @return
     */
    private Map<SootMethod, Integer> constructOutDegreeMap(CallGraph callGraph, Set<SootMethod> methodSet) {
        Map<SootMethod, Integer> outDegreeMap = new HashMap<SootMethod, Integer>();// get
        // outDegreeMap
        for (SootMethod m : methodSet) {
            if (!SootUtils.hasSootActiveBody(m))
                continue;
            outDegreeMap.put(m, 0); // initial outDegreeMap
        }
        for (Edge edge : callGraph) {
            if (!SootUtils.hasSootActiveBody(edge.getTgt().method()))
                continue;
            if (outDegreeMap.containsKey(edge.getSrc().method()))
                outDegreeMap.put(edge.getSrc().method(), outDegreeMap.get(edge.src()) + 1);
        }
        return outDegreeMap;
    }

    /**
     * constructInDregreeMap
     *
     * @param callGraph call graph
     * @param methodSet method set
     * @return
     */
    private Map<SootMethod, Integer> constructInDegreeMap(CallGraph callGraph, Set<SootMethod> methodSet) {
        Map<SootMethod, Integer> inDegreeMap = new HashMap<SootMethod, Integer>();// get
        // outDegreeMap
        for (SootMethod m : methodSet) {
            if (!SootUtils.hasSootActiveBody(m))
                continue;
            inDegreeMap.put(m, 0); // initial outDegreeMap
        }
        for (Edge edge : callGraph) {
            if (!SootUtils.hasSootActiveBody(edge.getSrc().method()))
                continue;
            if (inDegreeMap.containsKey(edge.getTgt().method()))
                inDegreeMap.put(edge.getTgt().method(), inDegreeMap.get(edge.tgt()) + 1);
        }
        return inDegreeMap;
    }
}