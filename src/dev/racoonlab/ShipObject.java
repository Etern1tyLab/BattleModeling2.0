package dev.racoonlab;

/**
 * Created by Lab on 31.01.2015.
 */
public class ShipObject {

    private String name;
    private int closeWeaponCount;
    private int middleWeaponCount;
    private int longWeaponCount;
    private float shield;
    private float shieldRegen;
    private float armor;
    private float armorRegen;

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
}
