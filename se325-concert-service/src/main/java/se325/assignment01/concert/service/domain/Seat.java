package se325.assignment01.concert.service.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Seat entity store the label of the seat, the booking status, date of the concert and its price
 */
@Entity
public class Seat implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String label;
	private boolean isBooked;
	private LocalDateTime date;
	private BigDecimal price;

	@Version
	private long version;

	public void setVersion(long version) {this.version = version;}

	public long getVersion() {return version;}

	public Seat() {
	}

	public Long getId() {return id;};

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean getIsBooked() { return isBooked;	}

	public void setIsBooked(boolean isBooked) {
		this.isBooked = isBooked;
	}

	public LocalDateTime getDate() { return date;	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return label;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass()) return false;

		Seat seat = (Seat) o;

		return new EqualsBuilder()
				.append(label, seat.label)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(label)
				.toHashCode();
	}
	public Seat(String label, boolean isBooked, LocalDateTime date, BigDecimal price) {
		this.label = label;
		this.isBooked = isBooked;
		this.date = date;
		this.price = price;
	}
}
