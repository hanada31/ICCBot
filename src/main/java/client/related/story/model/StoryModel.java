package main.java.client.related.story.model;

import main.java.client.obj.model.atg.ATGModel;

public class StoryModel {
	private ATGModel storyAtgModel;
	private String storyFilePath;

	public StoryModel() {
		setStoryAtgModel(new ATGModel());
	}

	/**
	 * @return the storyAtgModel
	 */
	public ATGModel getStoryAtgModel() {
		return storyAtgModel;
	}

	/**
	 * @param storyAtgModel
	 *            the storyAtgModel to set
	 */
	public void setStoryAtgModel(ATGModel storyAtgModel) {
		this.storyAtgModel = storyAtgModel;
	}

	/**
	 * @return the storyFilePath
	 */
	public String getStoryFilePath() {
		return storyFilePath;
	}

	/**
	 * @param storyFilePath
	 *            the storyFilePath to set
	 */
	public void setStoryFilePath(String storyFilePath) {
		this.storyFilePath = storyFilePath;
	}
}
