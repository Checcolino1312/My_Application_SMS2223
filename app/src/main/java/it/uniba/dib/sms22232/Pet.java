package it.uniba.dib.sms22232;

import java.util.HashMap;
import java.util.Map;

public class Pet {

    public static final String PETS = "pets";
    public static final String NAME = "name";
    public static final String AGE = "age";
    public static final String TYPE = "type";
    public static final String PICTURE = "picture";

    public static final String ID = "id";
    private static final String IMG = "image";
    private static final String TIPOLOGIA = "tipologia";

    private String id;
    private String name;
    private String age;
    private String type;
    private String tipologia;

    private String imgpet;


    public Pet(String id, String name, String age, String type, String tipologia) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.type = type;
        this.tipologia = tipologia;
    }

    public Pet(String id, String name, String age, String type, String tipologia, String imgGroup) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.type = type;
        this.tipologia = tipologia;
        this.imgpet = imgGroup;
    }

    public String getImgpet() {
        return imgpet;
    }

    public void setImgGroup(String imgGroup) {
        this.imgpet = imgGroup;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTipologia() {
        return tipologia;
    }

    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }

    public Map<String, Object> toMap() {//come se fosse uno schema dove si inseriscono tutti i punti fomìndamentali
        HashMap<String, Object> result = new HashMap<>();
        //new SimpleDateFormat("dd/MM/yyyy").format(creationDataGroup)

        result.put(ID, id);
        result.put(NAME, name);
        result.put(IMG, imgpet);
        result.put(TYPE, type);
        result.put(TIPOLOGIA, tipologia);
        result.put(AGE, age);


        return result;
    }

}
