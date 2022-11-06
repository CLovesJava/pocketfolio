package com.ssafy.pocketfolio.db.entity;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(
name="item",
uniqueConstraints = {
		@UniqueConstraint(name="UK_ITEM", columnNames={"key"}),
		@UniqueConstraint(name="UK_ITEM_CATEGORY", columnNames={"category", "name"})
}
)
public class Item {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="item_seq", nullable=false, updatable=false)
	private long itemSeq;
	
	@Column(name="name", length=20, nullable=false)
	@NotNull
	private String name;

	@Column(name="category", length=30, nullable=false)
	@NotNull
	private String category;

	@Column(name="key", length=50, nullable=false)
	@NotNull
	private String key;
	
	public void updateItem(String name, String category, String key) { // for administrator
		this.name = name;
		this.category = category;
		this.key = key;
	}
}