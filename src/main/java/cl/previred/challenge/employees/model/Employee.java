package cl.previred.challenge.employees.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

	private Long id;
	private String name;
	private String lastName;
	private String documentNumber;
	private String position;
	private Compensation compensation;
}