package entity;

import java.util.Date;

/**
 * person 表对应的实体类。
 * 数据库中的一行记录，在这里就是一个 PersonEntity 对象。
 */
public class PersonEntity {
    private String no;         // 唯一标识码
    private String name;       // 姓名
    private String sex;        // 性别
    private Date birthday;     // 出生日期
    private String professor;  // 职称
    private String deptno;     // 部门代码

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getDeptno() {
        return deptno;
    }

    public void setDeptno(String deptno) {
        this.deptno = deptno;
    }
}
