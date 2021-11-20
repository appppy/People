package jp.osaka.appppy.people.utils;

import android.content.Context;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import jp.osaka.appppy.people.constants.COLOR;
import jp.osaka.appppy.people.service.SimplePerson;

import static java.nio.charset.StandardCharsets.UTF_8;
import static jp.osaka.appppy.people.constants.COLOR.WHITE;
import static jp.osaka.appppy.people.constants.PeopleConstants.INVALID_LONG_VALUE;
import static jp.osaka.appppy.people.constants.PeopleConstants.INVALID_STRING_VALUE;

/**
 * Created by appy_000 on 2017/08/06.
 */
public class FileHelper {
    /**
     * ファイル書き込み
     *
     * @param context  コンテキスト
     * @param filename ファイル名
     * @param list     一覧
     */
    public static void writeFile(Context context, String filename, ArrayList<SimplePerson> list) {
        try {
            StringBuilder sb = new StringBuilder();
            FileOutputStream out = context.openFileOutput(filename, Context.MODE_PRIVATE);
            for (SimplePerson item : list) {
                sb.append(item.toCSV());
            }
            String data = sb.toString();
            out.write(data.getBytes());
            out.flush();
            out.close();
            sb.delete(0, sb.length());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ファイル読み込み
     *
     * @param context コンテキスト
     * @param file    ファイル
     * @return 一覧
     */
    public static ArrayList<SimplePerson> readFile(Context context, String file) {
        ArrayList<SimplePerson> result = new ArrayList<>();
        try {
            try (FileInputStream in = context.openFileInput(file)) {
                InputStreamReader ireader;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    ireader = new InputStreamReader(in, UTF_8);
                } else {
                    ireader = new InputStreamReader(in);
                }
                CSVReader reader = new CSVReader(ireader, ',', '"', 0);
                List<String[]> records = reader.readAll();
                for (String[] record : records) {
                    if (record.length > 12) {
                        try {
                            String path;
                            File out = new File(record[7]);
                            if (out.exists()) {
                                path = record[7];
                            } else {
                                path = INVALID_STRING_VALUE;
                            }
                            SimplePerson person = new SimplePerson(
                                    record[0],
                                    record[1],
                                    Long.parseLong(record[2]),
                                    Long.parseLong(record[3]),
                                    record[4],
                                    record[5],
                                    record[6],
                                    path,
                                    record[8],
                                    Boolean.parseBoolean(record[9]),
                                    Boolean.parseBoolean(record[10]),
                                    COLOR.valueOf(record[11]),
                                    Long.parseLong(record[12])
                            );
                            result.add(person);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } else if (record.length > 10) {
                        try {
                            String path;
                            File out = new File(record[7]);
                            if (out.exists()) {
                                path = record[7];
                            } else {
                                path = INVALID_STRING_VALUE;
                            }
                            SimplePerson person = new SimplePerson(
                                    record[0],
                                    record[1],
                                    Long.parseLong(record[2]),
                                    Long.parseLong(record[3]),
                                    record[4],
                                    record[5],
                                    record[6],
                                    path,
                                    record[8],
                                    Boolean.parseBoolean(record[9]),
                                    Boolean.parseBoolean(record[10]),
                                    WHITE,
                                    INVALID_LONG_VALUE
                            );
                            result.add(person);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
                reader.close();
                ireader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * ファイル読み込み
     *
     * @param file ファイル
     * @return 一覧
     */
    public static ArrayList<SimplePerson> readFile(File file) {
        ArrayList<SimplePerson> result = new ArrayList<>();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            CSVReader reader = new CSVReader(br, ',', '"', 0);
            List<String[]> records = reader.readAll();
            for (String[] record : records) {
                if (record.length == 2) {
                    try {
                        SimplePerson person = SimplePerson.createInstance();
                        person.displayName = record[0];
                        person.note = record[1];
                        result.add(person);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else if (record.length > 12) {
                    try {
                        String path;
                        File out = new File(record[7]);
                        if (out.exists()) {
                            path = record[7];
                        } else {
                            path = INVALID_STRING_VALUE;
                        }
                        SimplePerson person = new SimplePerson(
                                record[0],
                                record[1],
                                Long.parseLong(record[2]),
                                Long.parseLong(record[3]),
                                record[4],
                                record[5],
                                record[6],
                                path,
                                record[8],
                                Boolean.parseBoolean(record[9]),
                                Boolean.parseBoolean(record[10]),
                                COLOR.valueOf(record[11]),
                                Long.parseLong(record[12])
                        );
                        result.add(person);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else if (record.length > 10) {
                    try {
                        String path;
                        File out = new File(record[7]);
                        if (out.exists()) {
                            path = record[7];
                        } else {
                            path = INVALID_STRING_VALUE;
                        }
                        SimplePerson person = new SimplePerson(
                                record[0],
                                record[1],
                                Long.parseLong(record[2]),
                                Long.parseLong(record[3]),
                                record[4],
                                record[5],
                                record[6],
                                path,
                                record[8],
                                Boolean.parseBoolean(record[9]),
                                Boolean.parseBoolean(record[10]),
                                WHITE,
                                INVALID_LONG_VALUE
                        );
                        result.add(person);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * ファイルの削除
     *
     * @param context コンテキスト
     * @param name    ファイル名
     */
    public static void deleteFile(Context context, String name) {
        try {
            context.deleteFile(name + ".csv");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
