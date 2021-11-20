package jp.osaka.appppy.people.ui.content.recent;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jp.osaka.appppy.people.service.SimplePerson;

import static jp.osaka.appppy.people.constants.PeopleConstants.INVALID_LONG_VALUE;

/**
 * ヘルパ
 */
class RecentHelper {

    /**
     * タイムスタンプ一覧の取得
     *
     * @param collection 一覧
     * @return タイムスタンプ一覧
     */
    public static ArrayList<SimplePerson> toList(Collection<SimplePerson> collection) {
        ArrayList<SimplePerson> result = new ArrayList<>();
        for (SimplePerson item : collection) {
            if (!item.isArchive && !item.isTrash && !(item.timestamp == INVALID_LONG_VALUE)) {
                item.title = item.displayName;
                Date date = new Date(item.timestamp);
                item.subtitle = DateFormat.getDateTimeInstance().format(date);
                result.add(item);
            }
        }
        toSortByCollection(result);
        Collections.reverse(result);
        return result;
    }


    /**
     * タイムスタンプでソートした一覧取得
     *
     * @param collection 一覧
     */
    private static void toSortByCollection(Collection<SimplePerson> collection) {
        Collections.sort((List<SimplePerson>) collection, (lhs, rhs) -> (int) (lhs.timestamp - rhs.timestamp));
    }

}
