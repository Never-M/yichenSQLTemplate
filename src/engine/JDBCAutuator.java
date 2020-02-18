package engine;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

/**
 *
 * @author zjz20
 */

public class JDBCAutuator {

        private static final boolean PRINT_JDBC = false;
        
        public static class ParsedPositions {

                private final Map<String, List<Integer>> positions;

                public ParsedPositions(Map<String, List<Integer>> positions) {
                        this.positions = positions;
                }

                private List<Integer>
                        retrievePos(String name) {
                        List<Integer> pos = positions.get(name);
                        if (pos == null)
                                return new ArrayList();
                        return pos;
                }

                public ParsedPositions
                        setLong(PreparedStatement stmt, String name, Long val) throws SQLException {
                        for (int p : retrievePos(name))
                                if (val != null) stmt.setLong(p, val);
                                else             stmt.setNull(p, java.sql.Types.BIGINT);
                        return this;
                }
                        
                public ParsedPositions
                        setLong(PreparedStatement stmt, String name, Optional<Long> val) throws SQLException {
                        for (int p : retrievePos(name))
                                if (val.isPresent())    stmt.setLong(p, val.get());
                                else                    stmt.setNull(p, java.sql.Types.BIGINT);
                        return this;
                }

                public ParsedPositions
                        setInt(PreparedStatement stmt, String name, Integer val) throws SQLException {
                        for (int p : retrievePos(name))
                                if (val != null)  stmt.setInt(p, val);
                                else              stmt.setNull(p, java.sql.Types.INTEGER);
                        return this;
                }
                        
                public ParsedPositions
                        setInt(PreparedStatement stmt, String name, Optional<Integer> val) throws SQLException {
                        for (int p : retrievePos(name))
                                if (val.isPresent())    stmt.setInt(p, val.get());
                                else                    stmt.setNull(p, java.sql.Types.INTEGER);
                        return this;
                }

                public ParsedPositions
                        setString(PreparedStatement stmt, String name, String val) throws SQLException {
                        for (int p : retrievePos(name))
                                if (val != null) stmt.setString(p, val);
                                else             stmt.setNull(p, java.sql.Types.VARCHAR);
                        return this;
                }
                        
                public ParsedPositions
                        setString(PreparedStatement stmt, String name, Optional<String> val) throws SQLException {
                        for (int p : retrievePos(name))
                                if (val.isPresent())    stmt.setString(p, val.get());
                                else                    stmt.setNull(p, java.sql.Types.VARCHAR);
                        return this;
                }

                public ParsedPositions
                        setArray(PreparedStatement stmt, String name, Array val) throws SQLException {
                        for (int p : retrievePos(name))
                                if (val != null) stmt.setArray(p, val);
                                else             stmt.setNull(p, java.sql.Types.ARRAY);
                        return this;
                }
                        
                public ParsedPositions
                        setArray(PreparedStatement stmt, String name, Optional<Array> val) throws SQLException {
                        for (int p : retrievePos(name))
                                if (val.isPresent())    stmt.setArray(p, val.get());
                                else                    stmt.setNull(p, java.sql.Types.ARRAY);
                        return this;
                }

                public ParsedPositions 
                        setDate(PreparedStatement stmt, String name, Date val) throws SQLException {
                        for (int p : retrievePos(name))
                                if (val != null) stmt.setTimestamp(p, toTimestamp(val));
                                else             stmt.setNull(p, java.sql.Types.TIMESTAMP);
                        return this;
                }
                        
                public ParsedPositions 
                        setDate(PreparedStatement stmt, String name, Optional<Date> val) throws SQLException {
                        for (int p : retrievePos(name))
                                if (val.isPresent())    stmt.setTimestamp(p, toTimestamp(val.get()));
                                else                    stmt.setNull(p, java.sql.Types.TIMESTAMP);
                        return this;
                }

                public ParsedPositions 
                        setBool(PreparedStatement stmt, String name, Boolean val) throws SQLException {
                        for (int p : retrievePos(name))
                                if (val != null) stmt.setBoolean(p, val);
                                else             stmt.setNull(p, java.sql.Types.BOOLEAN);
                        return this;
                }
                        
                public ParsedPositions 
                        setBool(PreparedStatement stmt, String name, Optional<Boolean> val) throws SQLException {
                        for (int p : retrievePos(name))
                                if (val.isPresent())    stmt.setBoolean(p, val.get());
                                else                    stmt.setNull(p, java.sql.Types.BOOLEAN);
                        return this;
                }
        }
        
        public static class Info {
                
                private final TreeMap<Integer, Integer> namedParamsInfo;

                public Info(TreeMap<Integer, Integer> namedParamsInfo) {
                        this.namedParamsInfo = namedParamsInfo;
                }

                public TreeMap<Integer, Integer> 
                        getNamedParamsInfo() {
                        return namedParamsInfo;
                }

                @Override
                public int 
                        hashCode() {
                        int hash = 7;
                        hash = 11 * hash + Objects.hashCode(this.namedParamsInfo);
                        return hash;
                }

                @Override
                public boolean 
                        equals(Object obj) {
                        if (this == obj)
                                return true;
                        if (obj == null)
                                return false;
                        if (getClass() != obj.getClass())
                                return false;
                        final Info other = (Info) obj;
                        return Objects.equals(this.namedParamsInfo, other.namedParamsInfo);
                }
        }

        public static Timestamp
                toTimestamp(Date date) {
                return new Timestamp(date.getTime());
        }

        public static Info
                capture(String sql) {
                TreeMap<Integer, Integer> tokens = new TreeMap();
                boolean escaped = false;
                for (int i = 0; i < sql.length(); i++) {
                        if (!escaped && sql.charAt(i) == ':') {
                                int j;
                                for (j = i + 1; j < sql.length(); j++)
                                        if (!Character.isAlphabetic(sql.charAt(j)))
                                                break;
                                tokens.put(i, j - i);
                        }
                        escaped = sql.charAt(i) == '\\';
                }
                return new Info(tokens);
        }

        public static String
                toJDBC(Info info, String sql) {
                StringBuilder jdbc = new StringBuilder();
                int b = 0;
                for (Map.Entry e : info.getNamedParamsInfo().entrySet()) {
                        int k = (int) e.getKey();
                        int l = (int) e.getValue();
                        jdbc.append(sql.substring(b, k).replace("\\", "")).append("?");
                        b = k + l;
                }
                jdbc.append(sql.substring(b, sql.length()));
                return jdbc.toString();
        }

        public static ParsedPositions
                parsePositions(Info info, String sql) {
                Map<String, List<Integer>> parsed = new HashMap();
                int p = 0;
                for (Map.Entry e : info.getNamedParamsInfo().entrySet()) {
                        int k = (int) e.getKey();
                        int l = (int) e.getValue();
                        String key = sql.substring(k + 1, k + l);

                        List<Integer> positions = parsed.get(key);
                        if (positions == null) {
                                positions = new ArrayList();
                                positions.add(++ p);
                                parsed.put(key, positions);
                        } else
                                positions.add(++ p);
                }
                return new ParsedPositions(parsed);
        }

        public static Array
                intArray(int[] a, Connection conn) throws SQLException {
                if (a == null)
                        return null;
                Object[] objs = new Object[a.length];
                for (int i = 0; i < a.length; i++)
                        objs[i] = a[i];
                return conn.createArrayOf("int", objs);
        }
                
        public static Optional<Array>
                intArray(Collection<Integer> a, Connection conn) throws SQLException {
                if (a == null)
                        return Optional.empty();
                return Optional.of(conn.createArrayOf("int", a.toArray()));
        }

        public static Optional<Array>
                longArray(long[] a, Connection conn) throws SQLException {
                if (a == null)
                        return Optional.empty();
                Object[] objs = new Object[a.length];
                for (int i = 0; i < a.length; i++)
                        objs[i] = a[i];
                return Optional.of(conn.createArrayOf("bigint", objs));
        }

        public static Optional<Array>
                longArray(Collection<Long> a, Connection conn) throws SQLException {
                if (a == null)
                        return Optional.empty();
                return Optional.of(conn.createArrayOf("bigint", a.toArray()));
        }
                
        public static Optional<Array>
                stringArray(String[] str, Connection conn) throws SQLException {
                if (str == null)
                        return Optional.empty();
                return Optional.of(conn.createArrayOf("varchar", str));
        }
                
        public static Optional<Array>
                stringArray(Collection<String> str, Connection conn) throws SQLException {
                if (str == null)
                        return Optional.empty();
                return Optional.of(conn.createArrayOf("bigint", str.toArray()));
        }
                
        public static Long
                getLong(ResultSet rs, String selector) throws SQLException {
                Long v = rs.getLong(selector);
                return rs.wasNull() ? null : v;
        }
                
        public static Integer
                getInt(ResultSet rs, String selector) throws SQLException {
                Integer v = rs.getInt(selector);
                return rs.wasNull() ? null : v;
        }
                
        public static void 
                printJDBC(String jdbc, Class c) {
                if (PRINT_JDBC) {
                        System.out.println("Native JDBC from " + c.getName() + " -> " + jdbc);
                }
        }               
}