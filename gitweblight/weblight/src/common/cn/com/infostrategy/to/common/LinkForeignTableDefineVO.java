package cn.com.infostrategy.to.common;

import java.io.Serializable;

public class LinkForeignTableDefineVO
    implements Serializable
{

    public LinkForeignTableDefineVO()
    {
        tablename = null;
        colnames = null;
        foreignField = null;
        parentField = null;
    }

    public LinkForeignTableDefineVO(String _tablename, String _colnames, String _foreignField, String _parentField)
    {
        tablename = null;
        colnames = null;
        foreignField = null;
        parentField = null;
        tablename = _tablename;
        colnames = _colnames;
        foreignField = _foreignField;
        parentField = _parentField;
    }

    public String getTablename()
    {
        return tablename;
    }

    public void setTablename(String tablename)
    {
        this.tablename = tablename;
    }

    public String getColnames()
    {
        return colnames;
    }

    public void setColnames(String _colnames)
    {
        this.colnames = _colnames;
    }

    public String getForeignField()
    {
        return foreignField;
    }

    public void setForeignField(String foreignField)
    {
        this.foreignField = foreignField;
    }

    public String getParentField()
    {
        return parentField;
    }

    public void setParentField(String parentField)
    {
        this.parentField = parentField;
    }

    public String getSQL(String _parentfieldvalues[])
    {
        StringBuffer sb_sql = new StringBuffer();
        sb_sql.append("select ");
        boolean bo_haveid = false;
        String str_colitems[] = colnames.split(",");
        for(int i = 0; i < str_colitems.length; i++)
        {
            if(!str_colitems[i].trim().equalsIgnoreCase(foreignField.trim()))
                continue;
            bo_haveid = true;
            break;
        }

        if(!bo_haveid)
            sb_sql.append((new StringBuilder(String.valueOf(foreignField.toLowerCase()))).append(",").toString());
        for(int i = 0; i < str_colitems.length; i++)
        {
            sb_sql.append(str_colitems[i].trim().toLowerCase());
            if(i != str_colitems.length - 1)
                sb_sql.append(",");
        }

        sb_sql.append((new StringBuilder(" from ")).append(tablename.toLowerCase()).append(" where ").append(foreignField.toLowerCase()).append(" in (").toString());
        for(int i = 0; i < _parentfieldvalues.length; i++)
        {
            sb_sql.append((new StringBuilder("'")).append(_parentfieldvalues[i]).append("'").toString());
            if(i != _parentfieldvalues.length - 1)
                sb_sql.append(",");
        }

        sb_sql.append(")");
        return sb_sql.toString();
    }

    private String tablename;
    private String colnames;
    private String foreignField;
    private String parentField;
}
