# sdm_todolist_jsf_jdbc_sqlite3

JSF SPA project demonstrating how to use [SQL DAL Maker](https://github.com/panedrone/sqldalmaker) + Java/JDBC.

![demo-jsf-1.png](demo-jsf-1.png)

sdm.xml

```xml

<sdm>

    <dto-class name="Group" ref="groups">

        <field type="Integer" column="g_tasks_count"/>

    </dto-class>

    <dto-class name="Task" ref="tasks"/>

    <dao-class name="GroupsDao">

        <crud dto="Group">
            <create/>
            <update/>
            <delete/>
        </crud>

        <query-dto-list ref="get_groups.sql" dto="Group" method="getGroups()"/>

    </dao-class>

    <dao-class name="TasksDao">

        <crud dto="Task">
            <create/>
            <update/>
            <delete/>
        </crud>

        <query-dto-list ref="get_group_tasks.sql" dto="Task" method="getGroupTasks(int t_id)"/>

    </dao-class>

</sdm>
```
Generated DAO class:

```java
public class GroupsDao {

    protected final DataStore ds;

    public GroupsDao(DataStore ds) {
        this.ds = ds;
    }

    public int createGroup(Group p) throws Exception {
        String sql = "insert into groups (g_name) values (?)";
        String[] gen_col_nm = new String[]{"g_id"};
        Object[] gen_values = new Object[gen_col_nm.length];
        int res = ds.insert(sql, gen_col_nm, gen_values, p.getGName());
        p.setGId(ds.castGeneratedValue(Integer.class, gen_values[0]));
        return res;
    }

    public int updateGroup(Group p) throws Exception {
        String sql = "update groups set g_name=? where g_id=?";
        return ds.execDML(sql, p.getGName(), p.getGId());
    }

    public int deleteGroup(Integer gId) throws Exception {
        String sql = "delete from groups where g_id=?";
        return ds.execDML(sql, gId);
    }

    public List<Group> getGroups() throws Exception {
        String sql = "select g.*," +
                "\n (select count(*) from tasks where g_ID=g.g_ID) as tasks_count" +
                "\n from groups g";
        final List<Group> res = new ArrayList<Group>();
        ds.queryAllRows(sql, new DataStore.RowHandler() {
            @Override
            public void handleRow(DataStore.RowData rd) throws Exception {
                Group obj = new Group();
                obj.setGId(rd.getValue(Integer.class, "g_id"));  // t <- q
                obj.setGName(rd.getValue(String.class, "g_name"));  // t <- q
                res.add(obj);
            }
        });
        return res;
    }
}
```
