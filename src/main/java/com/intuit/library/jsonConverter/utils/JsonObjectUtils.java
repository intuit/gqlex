package com.intuit.library.jsonConverter.utils;

import com.intuit.library.jsonConverter.ParserConsts;
import com.intuit.library.jsonConverter.model.ReservedInternJsonWords;
import graphql.com.google.common.base.Preconditions;
import graphql.com.google.common.base.Strings;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class JsonObjectUtils {

    public static final String EMPTY_STRING = "";
    public static final String NAME = "name";
    public static final String VALUE = "value";

    /*
    use cases:

    parent has reserved name and only one value , this son has name of reserved with json beneath    ->
                reduce :
                    content -> single
                    single->single
                    content->content
    parent has reserved name and only one value , this value has name of non-reserved with json beneath
                reduce:
                    removed reserved , replace by non-reserved
    parent has non-reserved name and only one value , this value has name of reserved with json beneath
                reduce:
                     name wil be the   non-reserved name and value will be the json beneath
    parent has non-reserved name and only one value , this value has name of non-reserved with json beneath
                reduce:
                    only if the non-reserved names are the equal

    parent has name and > 1 values , this son has name of reserved with json beneath
                do nothing
    parent has name and > 1 values , this sone has name of non-reserved with json beneath
                do nothing

    */
    public static void reduceAdd(JSONObject parent, JSONObject son) {

        JsonKeysStructure sonJsonKeysStructure = getJsonKeysStructure(son);
        if (sonJsonKeysStructure.getKeysSize() > 1){
            // do nothing
            addToJsonObject(parent,ParserConsts.CONTENT, son);
            return;
        }


        JsonKeysStructure parentJsonKeysStructure = getJsonKeysStructure(parent);

        if (parentJsonKeysStructure.getKeysSize() > 1){
            // do nothing
            addToJsonObject(parent,sonJsonKeysStructure.getKeyName(), son.get(sonJsonKeysStructure.getKeyName()));
            return;
        }


        boolean isParentNameHasReservedName =  ReservedInternJsonWords.isContainmentLabel( parentJsonKeysStructure.getKeyName());
        boolean isSonNameHasReservedName = ReservedInternJsonWords.isContainmentLabel(sonJsonKeysStructure.getKeyName());

        boolean isParentHasValue = true;
        //boolean isJoinParentAndSonNoReduction = false;
        Object parentValueObj = parent.get(parentJsonKeysStructure.getKeyName());
        if( parentValueObj == null || parentValueObj == JSONObject.NULL ){
            // override parent value - in case parent contains no value, only key
            isParentHasValue = false;
           /* parent.put(parentJsonKeysStructure.getKeyName(),son);
            return;*/
        }

        boolean isParentHasOneVal = true;
        Optional<Boolean> isParentHasOneValue = isObjectContainsOnlyOneChildren(parentValueObj);

        isParentHasOneVal =  isParentHasOneValue.isPresent() ? isParentHasOneValue.get(): false;

        boolean parentNameIsOfSingleElement = ReservedInternJsonWords.isValueNameIsOfSingleElement(parentJsonKeysStructure.getKeyName());
        boolean sonNameIsOfSingleElement = ReservedInternJsonWords.isValueNameIsOfSingleElement(sonJsonKeysStructure.getKeyName());

        String balancedKey = getBalancedKey(parentNameIsOfSingleElement, sonNameIsOfSingleElement, parentJsonKeysStructure, sonJsonKeysStructure);

        if( !isParentHasValue){ //  no value in parent

            parent.put(balancedKey, son.get(sonJsonKeysStructure.getKeyName()));

        }else { // parent has value

         /*parent has reserved name and only one value , this son has name of reserved with json beneath    ->
        reduce :
        content -> single
        single->single
        content->content*/

            if (isParentNameHasReservedName && isParentHasOneVal && isSonNameHasReservedName) {

                parent.put(balancedKey, son.get(sonJsonKeysStructure.getKeyName()));
                return;
            }

        /*parent has reserved name and only one value , this value has name of non-reserved with json beneath
        reduce:
        removed reserved , replace by non-reserved*/
            if (isParentNameHasReservedName && isParentHasOneVal && !isSonNameHasReservedName) {
                parent.remove(parentJsonKeysStructure.getKeyName());
                parent.put(sonJsonKeysStructure.getKeyName(), son.get(sonJsonKeysStructure.getKeyName()));
                return;
            }

        /*parent has non-reserved name and only one value , this son has name of reserved with json beneath
        reduce:
        name wil be the   non-reserved name and value will be the json beneath*/
            if (!isParentNameHasReservedName && isParentHasOneVal && isSonNameHasReservedName) {
                parent.put(parentJsonKeysStructure.getKeyName(), son.get(sonJsonKeysStructure.getKeyName()));
                return;
            }


       /* parent has non-reserved name and only one value , son value has name of non-reserved with json beneath
        reduce:
        only if the non-reserved names are the equal*/
            if (!isParentNameHasReservedName && isParentHasOneVal && !isSonNameHasReservedName &&
                    parentJsonKeysStructure.getKeyName().equals(sonJsonKeysStructure.getKeyName())) {
                parent.put(parentJsonKeysStructure.getKeyName(), son.get(sonJsonKeysStructure.getKeyName()));

                return;
            }

        /*
        parent has name and > 1 values , this value has name of reserved with json beneath
                do nothing
    parent has name and > 1 values , this value has name of non-reserved with json beneath
                do nothing
         */

            if (!isParentNameHasReservedName && isParentHasOneVal && !isSonNameHasReservedName &&
                    parentJsonKeysStructure.getKeyName().equals(sonJsonKeysStructure.getKeyName())) {
                parent.put(parentJsonKeysStructure.getKeyName(), son.get(sonJsonKeysStructure.getKeyName()));
                return;
            }

            // parent.put(parentJsonKeysStructure.getKeyName(),son);
            parent.put(sonJsonKeysStructure.getKeyName(), son.get(sonJsonKeysStructure.getKeyName()));
            //addToJsonObject(parent, sonJsonKeysStructure.getKeyName(), son.get(sonJsonKeysStructure.getKeyName()));
        }
    }

    private static String getBalancedKey( boolean parentNameIsOfSingleElement, boolean sonNameIsOfSingleElement, JsonKeysStructure parentJsonKeysStructure, JsonKeysStructure sonJsonKeysStructure){
        if (!parentNameIsOfSingleElement && sonNameIsOfSingleElement) { //global -> single
            return parentJsonKeysStructure.getKeyName();
        }
        if (parentNameIsOfSingleElement && !sonNameIsOfSingleElement) { //single -> global
            return sonJsonKeysStructure.getKeyName();
        }

        if (parentNameIsOfSingleElement && sonNameIsOfSingleElement) { //single -> single
            return  sonJsonKeysStructure.getKeyName();
        }

        if (!parentNameIsOfSingleElement && !sonNameIsOfSingleElement) { //global - > global
            return parentJsonKeysStructure.getKeyName();
        }

        return parentJsonKeysStructure.getKeyName();
    }

    private static Optional<Boolean> isObjectContainsOnlyOneChildren(Object o) {
        if( o == null ){
            return null;
        }
        if( o instanceof JSONObject){
            JSONObject o1 = (JSONObject) o;
            if( o1.keySet() != null ){
                return Optional.of(o1.keySet().size() == 1 ? true : false);
            }
        }
        return Optional.of(true);
    }


    public static JsonKeysStructure getJsonKeysStructure(JSONObject jsonObject){
        Preconditions.checkNotNull(jsonObject);

        Set<String> keys = jsonObject.keySet();

        JsonKeysStructure jsonKeysStructure = new JsonKeysStructure();
        if( keys == null || keys.size() == 0){
            jsonKeysStructure.setKeysSize(0);
            return jsonKeysStructure;
        }

        jsonKeysStructure.setKeysSize(keys.size());
        if(keys.size() > 1){
            return jsonKeysStructure;
        }

        jsonKeysStructure.setKeyName(jsonObject.keys().next());

        return jsonKeysStructure;
    }
    public static void addJsonObjectToParentJsonObject(JSONObject parent, JSONObject addme){
        addJsonObjectToParentJsonObject( parent,  addme, ParserConsts.CONTENT);
    }
    /*
    parent is null
    addme is null
    key is null or empty
    addme keys is null
    addme keys is empty
    size>1
        size==2
            name and value
            only name
            only value
        else
           value is json

    size = 1
        keu is reserved
        key is not reserved and parent has already the same key
        key is unique , add the key with value to the parent
     */
    public static void addJsonObjectToParentJsonObject(JSONObject parent, JSONObject addme, String key){

        // verification
        if( parent == null ){
            throw new IllegalArgumentException("parent jsonObject is null");
        }

        if( addme == null ){
            throw new IllegalArgumentException("addme jsonObject is null");
        }

        if( addme.keySet() == null){
            return;
        }

        int addmeKeysSize = addme.keySet().size();

        if( addmeKeysSize == 0){
            return;
        }

        List<String> addmeKeyList  =new ArrayList<>();
        for (String addmeKey : addme.keySet()) {
            addmeKeyList.add(addmeKey.trim().toLowerCase());
        }

        if (addmeKeysSize > 1) {

            if( addmeKeysSize == 2){
                Boolean nameNameExist = addmeKeyList.contains(NAME);
                Boolean valueNameExist = addmeKeyList.contains(VALUE);

                if( nameNameExist && valueNameExist){
                    JsonObjectUtils.addToJsonObject(parent, addme.getString(NAME), addme.get(VALUE));
                   return;
                }
            }


            boolean enforceAddJsonObject = false;
            if( addme instanceof  JSONObject){
                enforceAddJsonObject = true;
            }

            JsonObjectUtils.addToJsonObject(parent, key, addme, enforceAddJsonObject, false);

        } else { // == 1

            boolean isParentKeyNameCanBeReplace = false; // only for  var, arg, field, elem that are replaceable
            int sizeOfParentKeys = parent.keySet().size();
            if( sizeOfParentKeys > 0) {
                boolean isParentKeyNameSingle = (sizeOfParentKeys == 1);
                String parentKeyName = parent.keys().next();
                if (isParentKeyNameSingle) {
                    Object parentValue = parent.get(parentKeyName);
                    boolean isParentHaveValue = ! isJsonValueEmpty(parentValue);
                    isParentKeyNameCanBeReplace = ReservedInternJsonWords.isValueNameIsOfSingleElement(parentKeyName) &&
                            !isParentHaveValue;
                }
                // parent is replaceable
                if (isParentKeyNameCanBeReplace) {
                    parent.remove(parentKeyName);
                }
            }

            String keyIntern = addme.keys().next();
            //if( EnumUtils.isValidEnum(ReservedInternJsonWords.class,keyIntern)){ //ReservedInternJsonWords.isLabel("__content")
            if( ReservedInternJsonWords.isContainmentLabel(keyIntern)){
                JsonObjectUtils.addToJsonObject(parent, key, addme.get(keyIntern), false, ! isParentKeyNameCanBeReplace);

            }else if( parent.has(keyIntern)) {
                boolean enforceAddJsonObject = false;
                if (addme instanceof JSONObject) {
                    enforceAddJsonObject = true;
                }

                JsonObjectUtils.addToJsonObject(parent, key, addme, enforceAddJsonObject,! isParentKeyNameCanBeReplace);

            }else {
                JsonObjectUtils.addToJsonObject(parent, keyIntern, addme.get(keyIntern), false, ! isParentKeyNameCanBeReplace);
            }

        }
    }

    private static boolean isJsonValueEmpty(Object parentValue) {
        if( parentValue == null ){
            return true;
        }

        if( parentValue == JSONObject.NULL){
            return true;
        }

        if( parentValue instanceof String ){
            return parentValue.toString().isEmpty();
        }


        if( parentValue instanceof JSONObject ){
            JSONObject parentValueJsonObj = (JSONObject) parentValue;
            if( parentValueJsonObj.keySet() != null && parentValueJsonObj.keySet().size() > 0 ){

                return  false;
            }
        }

        return true;
    }

    public static void addToJsonObject(JSONObject targetJsonObj, String key, Object value){
        addToJsonObject(targetJsonObj,key, value, false, false);
    }

    public static void addToJsonObject(JSONObject parentJsonObj, String key, Object value,
                                       boolean enforceAddJsonObjectValue, boolean isCreateContent){
        if( parentJsonObj == null ){
            throw new IllegalArgumentException("targetJsonObj is null");
        }
        if(Strings.isNullOrEmpty(key) || key.trim().isEmpty()){
           //throw new IllegalArgumentException("key is null");
            key = ParserConsts.CONTENT;
        }

        //  boolean isParentEmpty = isJsonObjectEmpty(parentJsonObj);
        JSONObject parentTarget = parentJsonObj;
        if( isCreateContent ) {
            JSONObject contentJsonObject = new JSONObject();
            parentJsonObj.put(ParserConsts.CONTENT, contentJsonObject);
            parentTarget = contentJsonObject;
        }

        if(isJsonValueEmpty(value)) {
            if( !ReservedInternJsonWords.isValueNameIsOfSingleElement(key)) {
                parentTarget.put(key, JSONObject.NULL);
            }
            return;
        }

        if( (value instanceof JSONObject) && !enforceAddJsonObjectValue) {
            addJsonObjectToParentJsonObject(parentTarget, (JSONObject) value, key);
        }else {
            parentTarget.put(key, value);
        }


    }

    /*private static boolean isJsonObjectEmpty(JSONObject parentJsonObj) {
        if( parentJsonObj == null ){
            throw new IllegalArgumentException("parentJsonObj is null");
        }

        if( parentJsonObj.keySet() == null || parentJsonObj.keySet().size() == 0){
            return true;
        }
        return false;
    }*/


    private static class JsonKeysStructure {
        private int keysSize;
        private String keyName;

        public void setKeysSize(int keysSize) {
            this.keysSize = keysSize;
        }

        public int getKeysSize() {
            return keysSize;
        }

        public void setKeyName(String keyName) {
            this.keyName = keyName;
        }

        public String getKeyName() {
            return keyName;
        }
    }
}
