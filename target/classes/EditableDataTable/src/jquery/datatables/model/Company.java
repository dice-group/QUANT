package jquery.datatables.model;

public class Company {
	
	static int nextID = 17;

    public Company(String name, String address, String town)
    {
        id = nextID++;
        this.name = name;
        this.address = address;
        this.town = town;
        this.country = id%4; //simulation of country id    
    }
    
    public Company(String name, String address, String town, int country)
    {
        id = nextID++;
        this.name = name;
        this.address = address;
        this.town = town;
        this.country = country;    
    }

	private int id;
    private String name;
    private String address;
    private String town;
    private int country;

	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTown() {
		return town;
	}
	public void setTown(String town) {
		this.town = town;
	}
    public int getCountry() {
		return country;
	}
	public void setCountry(int country) {
		this.country = country;
	}
}
