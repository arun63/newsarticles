package tcd.ie.ir.newsarticles.entity;

/**
 * 
 * @author arun
 *
 */
public class QueryObject {
	
	private String num;
    private String title;
    private String desc;
    private String narr;
    
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getNarr() {
		return narr;
	}
	public void setNarr(String narr) {
		this.narr = narr;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append("num:").append(num)
		.append(" title:").append(title)
		.append(" desc:").append(desc)
		.append(" narr:").append(narr)
		.append("]");
		return sb.toString();
		
	}

}
