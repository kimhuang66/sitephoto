package edu.princeton.sitephoto;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.princeton.sitephoto.data.User;

public class Util {
    private static final Logger logger = LoggerFactory.getLogger(Util.class);

    public static void orderByUserSortName(LinkedHashMap<String, User> m, final Comparator<? super User> c) {
        List<Map.Entry<String, User>> entries = new ArrayList<>(m.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<String, User>>() {
            @Override
            public int compare(Map.Entry<String, User> lhs, Map.Entry<String, User> rhs) {
                String vl = ((User) lhs.getValue()).sort_name;
                String vr = ((User) rhs.getValue()).sort_name;
                return vl.compareTo(vr);
            }
        });
        m.clear();
        for (Map.Entry<String, User> e : entries) {
            m.put(e.getKey(), e.getValue());
        }
    }

    public static String strmToSemester(String strm) throws Exception {
        String semester = "";
        if (strm == null) {
            throw new Exception("strm is null");
        } else if (strm.length() != 4) {
            throw new Exception("strm is invalid length: strm=" + strm);
        } else {
            int strmYearIdentifier = Integer.parseInt(strm.substring(0, 3));
            int strmSemesterIdentifier = Integer.parseInt(strm.substring(3));

            switch (strmSemesterIdentifier) {
                case 1:
                    // summer - count this as belonging to previous spring
                    semester = "S" + (1900 + strmYearIdentifier - 1);
                    break;
                case 2:
                    // fall - summer and fall strms use the 'course year' so subtract 1
                    // from the yearIdentifier to get the 'semester year'
                    semester = "F" + (1900 + strmYearIdentifier - 1);
                    break;
                case 4:
                    // spring
                    semester = "S" + (1900 + strmYearIdentifier);
                    break;
                default:
                    // not recognized
                    throw new Exception("strm has unrecognized semester part: strm=" + strm);
            }
        }

        return semester;
    }

    /**
     * Convert a semester to an strm, for example: S2010 -> 1104 F2010 -> 1112 S2011
     * -> 1114 F2011 -> 1122 S2012 -> 1124
     * 
     * @param semester
     * @return an strm value for the semester, for example F2010 -> 1112
     * @throws Exception
     */
    public static String semesterToStrm(String semester) throws Exception {
        String strm = "";
        String semesterIdentifier = "";
        String semesterYear = "";
        if (semester == null) {
            throw new Exception("semester is null");
        } else if (!(semester.length() == 5 || semester.length() == 6)) {
            throw new Exception("semester is invalid length: semester=" + semester);
        } else {
            if (semester.length() == 5) {
                semesterIdentifier = semester.substring(0, 1);
                semesterYear = semester.substring(1);
            } else {
                semesterIdentifier = semester.substring(0, 2);
                semesterYear = semester.substring(2);

            }

            // compute strmValue as int
            String strmSemesterIdentifier = "";
            int strmSemesterYear = Integer.parseInt(semesterYear) - 1900;

            if (semesterIdentifier.equals("F")) {
                strmSemesterIdentifier = "2";
                strmSemesterYear++; // set to 'course year' for the fall semester
            } else if (semesterIdentifier.equals("S")) {
                strmSemesterIdentifier = "4";
            } else if (semesterIdentifier.equals("SU")) {
                strmSemesterIdentifier = "1";
                strmSemesterYear++;
            } else {
                throw new Exception(semester + " has unrecognized semester identifier=" + semesterIdentifier);
            }

            strm = "" + strmSemesterYear + strmSemesterIdentifier;
        }

        if (strm.equals("")) {
            throw new Exception("unable to calculate strm value from semester=" + semester);
        }

        return strm;
    }

    public static String classyearConvert(String classyear) {
        if (classyear == null) {
            return "";
        }
        return " '" + (String.valueOf(classyear)).substring(2);
    }

    public static String getCrsSection(String crs_id) {
        if ("".equals(crs_id)) {
            return "";
        }
        final String term = crs_id.split("_")[1];
        String first = crs_id.split("_")[0];
        String[] crsS = first.split("-");

        // it is a course
        if (crsS[1] != null && crsS[1].length() < 5) {
            return crsS[1];
        }
        return "";
    }

}