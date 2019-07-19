package models.portal;

public class InfoLast {
	private Integer countExtern;
	private Integer countIntern;
	private Integer countArchived;
	
	public InfoLast(Integer countExtern, Integer countIntern, Integer countArchived) {
		this.countExtern = countExtern;
		this.countIntern = countIntern;
		this.countArchived = countArchived;
	}

	public Integer getCountExtern() {
		return countExtern;
	}

	public Integer getCountIntern() {
		return countIntern;
	}

	public Integer getCountArchived() {
		return countArchived;
	}
}
