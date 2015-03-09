package dev.racoonlab;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import dev.racoonlab.WeaponObject;

import java.io.Serializable;
import java.util.*;


public class ShipObject implements Serializable {

    private String name;
    private int closeWeaponCount;
    private int middleWeaponCount;
    private int longWeaponCount;
    private double shield;
    private double currentShield;
    private double shieldRegen;
    private double armor;
    private double currentArmor;
    private double armorRegen;
	private double maxWeaponEnergy;

	public double getCurrentWeaponEnergy() {
		return currentWeaponEnergy;
	}

	public void setCurrentWeaponEnergy(double currentWeaponEnergy) {
		this.currentWeaponEnergy = currentWeaponEnergy;
	}

	public double getMaxWeaponEnergy() {
		return maxWeaponEnergy;
	}

	public void setMaxWeaponEnergy(double maxWeaponEnergy) {
		this.maxWeaponEnergy = maxWeaponEnergy;
	}

	private double currentWeaponEnergy;

	public int getMobility() {
		return mobility;
	}

	private int mobility;

    private int midSepartorIndex;
    private int longSepartorIndex;

    private List<WeaponObject> closeWeapons = new ArrayList<>();
    private List<WeaponObject> midWeapons = new ArrayList<>();
    private List<WeaponObject> longWeapons = new ArrayList<>();

    private ObservableList<String> currentWeaponsList = FXCollections.observableArrayList();

    public double getArmorRegen() {
        return armorRegen;
    }

    public double getShieldRegen() {
        return shieldRegen;
    }

    public double getCurrentArmor() {
        return Math.round(currentArmor * 100.0)/100.0;
    }

    public void setCurrentArmor(double currentArmor) {
        if (currentArmor < 0)
            this.currentArmor = 0;
        else
            this.currentArmor = Math.round(currentArmor * 100.0)/100.0;;
    }

    public double getCurrentShield() {
        return Math.round(currentShield * 100.0)/100.0;
    }

    public void setCurrentShield(double currentShield) {
        if (currentShield < 0)
            this.currentShield = 0;
        else
            this.currentShield = Math.round(currentShield * 100.0)/100.0;
    }

    public List<WeaponObject> getLongWeapons() {
        return longWeapons;
    }

    public List<WeaponObject> getCloseWeapons() {
        return closeWeapons;
    }

    public List<WeaponObject> getMidWeapons() {
        return midWeapons;
    }

    public int getLongSepartorIndex() {
        return longSepartorIndex;
    }

    public int getMidSepartorIndex() {
        return midSepartorIndex;
    }

    public String getName() {
        return name;
    }

    public int getCloseWeaponCount() {
        return closeWeaponCount;
    }

    public int getMiddleWeaponCount() {
        return middleWeaponCount;
    }

    public int getLongWeaponCount() {
        return longWeaponCount;
    }

    public double getShield() {
        return shield;
    }

    public double getArmor() {
        return armor;
    }

    public ObservableList<String> getCurrentWeaponsList ()
    {
        return currentWeaponsList;
    }


    public ShipObject getClone ()
    {
        return new ShipObject(this.name, this.closeWeaponCount, this.middleWeaponCount, this.longWeaponCount, this.shield, this.armor, this.shieldRegen, this.armorRegen, this.mobility, this.maxWeaponEnergy);
    }

    public ShipObject(String _name, int _closeWeaponCount, int _middleWeaponCount, int _longWeaponCount, double _shield, double _armor, double _shieldRegen, double _armorRegen, int _mobility, double _maxWeaponEnergy)
    {
        this.name = _name;
        this.closeWeaponCount = _closeWeaponCount;
        this.middleWeaponCount = _middleWeaponCount;
        this.longWeaponCount = _longWeaponCount;
        this.shield = _shield;
        this.armor = _armor;
        this.shieldRegen = _shieldRegen;
        this.armorRegen = _armorRegen;
        this.currentArmor = _armor;
        this.currentShield = _shield;
		this.mobility = _mobility;
		this.maxWeaponEnergy = _maxWeaponEnergy;
		this.currentWeaponEnergy = 0;
    }

    public void addWeapon(WeaponObject weaponObject)
    {
        switch (weaponObject.getDistance())
        {
            case 1:
            {
                closeWeapons.add(closeWeapons.size(),weaponObject);
                break;
            }
            case 2:
            {
                midWeapons.add(midWeapons.size(),weaponObject);
                break;
            }
            case 3:
            {
                longWeapons.add(longWeapons.size(),weaponObject);
                break;
            }
        }

        updateCurrentWeaponsList();

    }



    public void updateCurrentWeaponsList ()
    {
        currentWeaponsList.clear();
        currentWeaponsList.add("<Ближняя дистанция>");
        for (int i = 0; i < closeWeapons.size(); i++)
        {

            currentWeaponsList.add(this.closeWeapons.get(i).getName());
        }

        currentWeaponsList.add("<Средняя дистанция>");
        midSepartorIndex = currentWeaponsList.size() - 1;
        for (int i = 0; i < midWeapons.size(); i++)
        {

            currentWeaponsList.add(this.midWeapons.get(i).getName());
        }

        currentWeaponsList.add("<Дальняя дистанция>");
        longSepartorIndex = currentWeaponsList.size() - 1;
        for (int i = 0; i < longWeapons.size(); i++)
        {

            currentWeaponsList.add(this.longWeapons.get(i).getName());
        }

    }

    public boolean closeRangeAllowed ()
    {
        if (closeWeaponCount > closeWeapons.size())
            return true;
        else
            return false;
    }

    public boolean midRangeAllowed ()
    {
        if (middleWeaponCount > midWeapons.size())
            return true;
        else
            return false;
    }

    public boolean longRangeAllowed ()
    {
        if (longWeaponCount > longWeapons.size())
            return true;
        else
            return false;
    }

    public boolean isDead ()
    {
        if (currentArmor == 0 && currentShield ==0)
            return true;
        else
            return false;
    }

    public String currentCloseRangeCount ()
    {
        return String.valueOf(closeWeapons.size());
    }

    public String currentMidRangeCount ()
    {
        return String.valueOf(midWeapons.size());
    }

    public String currentLongRangeCount ()
    {
        return String.valueOf(longWeapons.size());
    }

}
