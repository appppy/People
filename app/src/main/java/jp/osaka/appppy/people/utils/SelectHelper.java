package jp.osaka.appppy.people.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.osaka.appppy.people.service.SimplePerson;

/**
 * Created by appy_000 on 2017/08/06.
 */

public class SelectHelper {

    /**
     * 選択状態の確認
     *
     * @return 選択状態
     */
    public static boolean isSelected(List<SimplePerson> collection) {
        boolean result = false;
        try {
            // 製品の選択状態を確認する
            for (SimplePerson person : collection) {
                // 選択状態を確認した
                if (person.isSelected) {
                    result = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 複数選択状態の確認
     *
     * @return 複数選択状態
     */
    public static boolean isMultiSelected(List<SimplePerson> collection) {
        int count = 0;
        try {
            // 製品の選択状態を確認する
            for (SimplePerson person : collection) {
                // 選択状態を確認した
                if (person.isSelected) {
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (count > 1);
    }

    /**
     * 選択一覧の取得
     *
     * @return 選択一覧
     */
    public static Collection<SimplePerson> getSelectedCollection(List<SimplePerson> list) {
        Collection<SimplePerson> collection = new ArrayList<>();
        try {
            for (SimplePerson person : list) {
                if (person.isSelected) {
                    collection.add(person);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return collection;
    }

    /**
     * 選択一覧の取得
     *
     * @return 選択一覧
     */
    public static Collection<SimplePerson> getSelectedCollection(Collection<? extends SimplePerson> collection) {
        Collection<SimplePerson> result = new ArrayList<>();
        try {
            for (SimplePerson item : collection) {
                if (item.isSelected) {
                    result.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
