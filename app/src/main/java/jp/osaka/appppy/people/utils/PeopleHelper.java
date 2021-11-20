package jp.osaka.appppy.people.utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import jp.osaka.appppy.people.service.SimplePerson;

import static jp.osaka.appppy.people.constants.PeopleConstants.MAX;

/**
 * ヘルパ
 */
public class PeopleHelper {
    /**
     * CSVに変換
     *
     * @param list 一覧
     * @return CSV
     */
    public static String toCSV(ArrayList<SimplePerson> list) {
        StringBuilder sb = new StringBuilder();
        try {
            for (SimplePerson item : list) {
                sb.append(item.toCSV());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String data = sb.toString();
        sb.delete(0, sb.length());
        return data;
    }

    /**
     * 最大数の確認
     *
     * @param list 一覧
     * @return 最大の有無
     */
    public static boolean isMax(List<SimplePerson> list) {
        boolean result = false;
        try {
            if (list.size() >= MAX) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 項目の取得
     *
     * @param uuid 識別子
     * @param list 一覧
     * @return 項目
     */
    public static SimplePerson getPerson(String uuid, List<SimplePerson> list) {
        SimplePerson result = null;
        try {
            for (SimplePerson dest : list) {
                if (uuid.equals(dest.uuid)) {
                    result = dest;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 一覧のコピー
     *
     * @param dest コピー先
     * @param src  コピー元
     */
    public static void copyPeople(ArrayList<SimplePerson> dest, ArrayList<SimplePerson> src) {
        try {
            dest.clear();
            for (SimplePerson s : src) {
                SimplePerson d = SimplePerson.createInstance();
                d.copyPerson(s);
                dest.add(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * {@inheritDoc}
     */
    public static boolean isModified(SimplePerson dest, SimplePerson src) {
        boolean result = false;
        try {
            if (dest.uuid.equals(src.uuid)) {
                if ((dest.creationDate != src.creationDate)
                        || (dest.modifiedDate != src.modifiedDate)
                        || (!dest.displayName.equals(src.displayName))
                        || (!dest.call.equals(src.call))
                        || (!dest.send.equals(src.send))
                        || (!dest.imagePath.equals(src.imagePath))
                        || (!dest.note.equals(src.note))
                        || (dest.isArchive != src.isArchive)
                        || (dest.isTrash != src.isTrash)) {
                    result = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * JSON文字変換
     *
     * @param collection 一覧
     * @return JSON文字
     */
    public static String toJSONString(ArrayList<SimplePerson> collection) {
        JSONArray array = new JSONArray();
        for (SimplePerson item : collection) {
            array.put(item.toJSONObject());
        }
        return array.toString();
    }

    /**
     * 人々変換
     *
     * @param JSONString JSON文字
     * @return 人々
     */
    public static ArrayList<SimplePerson> toPeople(String JSONString) {
        ArrayList<SimplePerson> results = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(JSONString);
            int count = array.length();
            for (int i=0; i<count; i++){
                results.add(new SimplePerson(array.getJSONObject(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}
