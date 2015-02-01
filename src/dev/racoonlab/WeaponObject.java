package dev.racoonlab;

/**
 * Created by Lab on 01.02.2015.
 */
public class WeaponObject {
    private String name;
    private int type;
    private float damage;
    private int cdTime;
    private int magazine;
    private int reloadTime;
    private int currentCd;
    private int currentReloadTime;
    private int currentMagazine;


    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public float getDamage() {
        return damage;
    }

    public int getCdTime() {
        return cdTime;
    }

    public int getMagazine() {
        return magazine;
    }

    public int getReloadTime() {
        return reloadTime;
    }

    public int getCurrentCd() {
        return currentCd;
    }

    public int getCurrentReloadTime() {
        return currentReloadTime;
    }

    public int getCurrentMagazine() {
        return currentMagazine;
    }






    public WeaponObject(String _name, int _type, float _damage, int _cdTime, int _magazine, int _reloadTime)
    {
        this.name = _name;
        this.type = _type;
        this.damage = _damage;
        this.cdTime = _cdTime;
        this.magazine = _magazine;
        this.reloadTime = _reloadTime;
        this.currentCd = 0;
        this.currentMagazine = _magazine;
        this.currentReloadTime = 0;
    }
}
