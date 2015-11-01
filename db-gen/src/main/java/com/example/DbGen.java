package com.example;

import java.io.IOException;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class DbGen {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1000, "com.snilius.suchquick.entity");


        Entity shortcut = schema.addEntity("Shortcut");
        shortcut.addIdProperty().autoincrement();
        shortcut.addStringProperty("UIName");
        shortcut.addStringProperty("Name");
        shortcut.addStringProperty("ClassName");
        shortcut.addStringProperty("PackageName");

        Entity extra = schema.addEntity("IntentExtra");
        extra.addIdProperty().getProperty();
        extra.addStringProperty("KeyName");
        extra.addStringProperty("StringProp");
        extra.addIntProperty("IntProp");
        Property shortcutId = extra.addLongProperty("shortcutId").notNull().getProperty();
        shortcut.addToMany(extra, shortcutId);

        new DaoGenerator().generateAll(schema, "/home/victor/AndroidStudioProjects/SuchQuick/app/src-gen");
    }
}
