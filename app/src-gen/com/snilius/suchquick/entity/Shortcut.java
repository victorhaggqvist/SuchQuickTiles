package com.snilius.suchquick.entity;

import java.util.List;
import com.snilius.suchquick.entity.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "SHORTCUT".
 */
public class Shortcut {

    private Long id;
    private String UIName;
    private String Name;
    private String ClassName;
    private String PackageName;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient ShortcutDao myDao;

    private List<IntentExtra> intentExtraList;

    public Shortcut() {
    }

    public Shortcut(Long id) {
        this.id = id;
    }

    public Shortcut(Long id, String UIName, String Name, String ClassName, String PackageName) {
        this.id = id;
        this.UIName = UIName;
        this.Name = Name;
        this.ClassName = ClassName;
        this.PackageName = PackageName;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getShortcutDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUIName() {
        return UIName;
    }

    public void setUIName(String UIName) {
        this.UIName = UIName;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String ClassName) {
        this.ClassName = ClassName;
    }

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String PackageName) {
        this.PackageName = PackageName;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<IntentExtra> getIntentExtraList() {
        if (intentExtraList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            IntentExtraDao targetDao = daoSession.getIntentExtraDao();
            List<IntentExtra> intentExtraListNew = targetDao._queryShortcut_IntentExtraList(id);
            synchronized (this) {
                if(intentExtraList == null) {
                    intentExtraList = intentExtraListNew;
                }
            }
        }
        return intentExtraList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetIntentExtraList() {
        intentExtraList = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
