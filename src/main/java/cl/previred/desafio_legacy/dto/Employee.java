package cl.previred.desafio_legacy.dto;

/**
 * 
 * Dto de employees.
 * 
 * @author Christopher Gaete Oliveres.
 * @version 1.0.0, 02/05/2026
 * 
 */
public class Employee {

	private Integer id;
	private String name;
	private String lastName;
	private String rut;
	private String charget;
	private Double salary;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getRut() {
		return rut;
	}

	public void setRut(String rut) {
		this.rut = rut;
	}

	public String getCharget() {
		return charget;
	}

	public void setCharget(String charget) {
		this.charget = charget;
	}

	public Double getSalary() {
		return salary;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}

	@Override
	public String toString() {
		return "Employees [id=" + id + ", name=" + name + ", LastName=" + lastName + ", rut=" + rut + ", charget="
				+ charget + ", salary=" + salary + "]";
	}

}
