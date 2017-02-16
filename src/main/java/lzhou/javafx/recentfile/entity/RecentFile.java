package lzhou.javafx.recentfile.entity;



import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//lombok annotations:
@Getter 
@Setter
@ToString
//JPA annotations:
@Entity
@Table(name="recentfiles")
public class RecentFile implements Serializable {
	/**
	 * 
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Integer id;
	@Column(name="filename")
	private String filename;
	
	public RecentFile() {
		
	}
	
	public RecentFile(String filename) {
		this.filename= filename;
	}
}
