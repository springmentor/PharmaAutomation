package com.pas.model;

public class Supplier {
	private int supplierId;
    private String name;
    private String address;
    private String phone;
    public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	private String email;
    
    public Supplier() {}

    public Supplier(String name, String address, String email,String phone) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
    }

    // Getters and Setters
    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
