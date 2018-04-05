package tcd.ie.ir.newsarticles.entity;

/**
 * 
 * @author arun
 *
 */

public class ResultantObject {
	
	private String rId;
    private float score;
    private int rank;

    public ResultantObject (String rId, float score, int rank) {
        this.setrId(rId);
        this.setScore(score);
        this.setRank(rank);
    }

	public String getrId() {
		return rId;
	}

	public void setrId(String rId) {
		this.rId = rId;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}


}
