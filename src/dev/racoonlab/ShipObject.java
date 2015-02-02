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
    private float shield;
    private float shieldRegen;
    private float armor;
    private float armorRegen;

    private int midSepartorIndex;
    private int longSepartorIndex;

    private List<WeaponObject> closeWeapons = new ArrayList<>();
    private List<WeaponObject> midWeapons = new ArrayList<>();
    private List<WeaponObject> longWeapons = new ArrayList<>();

    private ObservableList<String> currentWeaponsList = FXCollections.observableArrayList();

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

    public float getShield() {
        return shield;
    }

    public float getArmor() {
        return armor;
    }

    public ObservableList<String> getCurrentWeaponsList ()
    {
        return currentWeaponsList;
    }


    public ShipObject getClone ()
    {
        return new ShipObject(this.name, this.closeWeaponCount, this.middleWeaponCount, this.longWeaponCount, this.shield, this.armor, this.shieldRegen, this.armorRegen);
    }

    public ShipObject(String _name, int _closeWeaponCount, int _middleWeaponCount, int _longWeaponCount, float _shield, float _armor, float _shieldRegen, float _armorRegen)
    {
        this.name = _name;
        this.closeWeaponCount = _closeWeaponCount;
        this.middleWeaponCount = _middleWeaponCount;
        this.longWeaponCount = _longWeaponCount;
        this.shield = _shield;
        this.armor = _armor;
        this.shieldRegen = _shieldRegen;
        this.armorRegen = _armorRegen;
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
