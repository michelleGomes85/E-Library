package br.elibrary.dto;

import java.io.Serializable;

public class DashboardDTO implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
	private Integer totalBooks;
    private Integer totalCopies;
    private Integer totalAvailable;
    private Integer totalReserved;
    private Integer totalBorrowed;

    public DashboardDTO(
    		Integer totalBooks, 
    		Integer totalCopies, 
    		Integer totalAvailable, 
    		Integer totalReserved, 
    		Integer totalBorrowed) {
    	
        this.totalBooks = totalBooks;
        this.totalCopies = totalCopies;
        this.totalAvailable = totalAvailable;
        this.totalReserved = totalReserved;
        this.totalBorrowed = totalBorrowed;
    }

	public Integer getTotalBooks() {
		return totalBooks;
	}

	public void setTotalBooks(Integer totalBooks) {
		this.totalBooks = totalBooks;
	}

	public Integer getTotalCopies() {
		return totalCopies;
	}

	public void setTotalCopies(Integer totalCopies) {
		this.totalCopies = totalCopies;
	}

	public Integer getTotalAvailable() {
		return totalAvailable;
	}

	public void setTotalAvailable(Integer totalAvailable) {
		this.totalAvailable = totalAvailable;
	}

	public Integer getTotalReserved() {
		return totalReserved;
	}

	public void setTotalReserved(Integer totalReserved) {
		this.totalReserved = totalReserved;
	}

	public Integer getTotalBorrowed() {
		return totalBorrowed;
	}

	public void setTotalBorrowed(Integer totalBorrowed) {
		this.totalBorrowed = totalBorrowed;
	}
}
