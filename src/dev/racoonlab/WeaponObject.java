package dev.racoonlab;

public class WeaponObject {
    private String name;
    private int type;
    private double damage;
    private int magazine;
    private int reloadTime;
	private int rotationSpeed;
	private double wearOut;

	public double getDurability() {
		return durability;
	}

	public void setDurability(double durability) {
		this.durability = durability;
	}

	public double getEnergyConsumption() {
		return energyConsumption;
	}

	public void setEnergyConsumption(double energyConsumption) {
		this.energyConsumption = energyConsumption;
	}

	public double getWearOut() {
		return wearOut;
	}

	public void setWearOut(double wearOut) {
		this.wearOut = wearOut;
	}

	public int getRotationSpeed() {
		return rotationSpeed;
	}

	public void setRotationSpeed(int rotationSpeed) {
		this.rotationSpeed = rotationSpeed;
	}

	private double durability;
	private double energyConsumption;

    private int distance;
    private int currentReloadTime;
    private int currentMagazine;

    public void setCurrentMagazine(int currentMagazine) {
        this.currentMagazine = currentMagazine;
    }

    public void setCurrentReloadTime(int currentReloadTime) {
        this.currentReloadTime = currentReloadTime;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public double getDamage() {
        return damage;
    }

    public int getMagazine() {
        return magazine;
    }

    public int getReloadTime() {
        return reloadTime;
    }

    public int getDistance() {
        return distance;
    }

    public int getCurrentReloadTime() {
        return currentReloadTime;
    }

    public int getCurrentMagazine() {
        return currentMagazine;
    }


    public WeaponObject getClone ()
    {
        return new WeaponObject(this.name, this.type, this.damage, this.magazine, this.reloadTime, this.distance,this.wearOut, this.energyConsumption, this.rotationSpeed);
    }



    public WeaponObject(String _name, int _type, double _damage, int _magazine, int _reloadTime, int _distance, double _wearOut, double _energyConsumption ,int _rotationSpeed)
    {
        this.name = _name;
        this.type = _type;
        this.damage = _damage;
        this.magazine = _magazine;
        this.reloadTime = _reloadTime;
        this.distance = _distance;
        this.currentMagazine = _magazine;
		this.rotationSpeed = _rotationSpeed;
		this.wearOut = _wearOut;
		this.energyConsumption = _energyConsumption;
		this.durability = 0;
        this.currentReloadTime = 0;

    }

	public double getDamageInSec ()
	{
		return this.damage / this.magazine;
	}
}
