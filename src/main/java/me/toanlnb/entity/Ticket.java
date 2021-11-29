package me.toanlnb.entity;

import java.sql.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "Ticket", uniqueConstraints = @UniqueConstraint(columnNames = { "seat_number", "date", "bus_id" }))
public class Ticket {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "seat_number")
	private int seatNumber;

	@ManyToOne
	@JoinColumn(name = "bus_id", foreignKey = @ForeignKey(name = "fk_bus"))
	private Bus bus;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date date;

	private double price;

	public Bus getBus() {
		return bus;
	}

	public void setBus(Bus bus) {
		this.bus = bus;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(int seatNumber) {
		this.seatNumber = seatNumber;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
