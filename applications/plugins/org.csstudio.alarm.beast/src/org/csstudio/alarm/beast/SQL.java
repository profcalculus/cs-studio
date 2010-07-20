/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;

/** SQL Helper for alarm configuration and status info in RDB.
 *  @author Kay Kasemir, Xihui Chen
 */
@SuppressWarnings("nls")
public class SQL
{
    /** Schema prefix. Required for SNS Oracle. Set to "" for MySQL */
    final public String schema_prefix;

    final public String sel_item_by_name;
	final public String sel_guidance_by_id;
	final public String sel_displays_by_id;
	final public String sel_commands_by_id;
    final public String sel_items_by_parent;
    final public String sel_last_item_id;
    final public String insert_item;
    
	final public String delete_guidance_by_id;
	final public String insert_guidance;
	final public String delete_displays_by_id;
	final public String insert_display;
	final public String delete_commands_by_id;
	final public String insert_command;
		
	final public String update_item_config_time;
	
    final public String delete_component_by_id;
    
    final public String sel_pvs_by_parent;
    
    final public String sel_pv_by_id;
    final public String insert_pv;
   
    final public String update_pv_config;
    final public String update_pv_state;
    final public String update_pv_enablement;
    final public String delete_pv_by_id;
    final public String rename_item;
    final public String move_item;
    
    final public String sel_severity;
    final public String sel_last_severity;
    final public String insert_severity;

    final public String severity_table = "SEVERITY";
    final public String severity_id_col = "SEVERITY_ID";
    final public String severity_name_col = "NAME";

    final public String message_table = "STATUS";
    final public String message_id_col = "STATUS_ID";
    final public String message_name_col = "NAME";
    
    /** Initialize
     *  @param rdb RDBUtil
     */
    public SQL(final RDBUtil rdb) throws Exception
    {
        if (rdb.getDialect() == Dialect.MySQL)
            schema_prefix = "";
        else if (rdb.getDialect() == Dialect.Oracle)
            schema_prefix = "ALARM.";
        else
        	throw new Exception("This database is not supported");
        
        sel_item_by_name = "SELECT COMPONENT_ID, PARENT_CMPNT_ID FROM " + schema_prefix + "ALARM_TREE WHERE NAME=?";
        sel_guidance_by_id = 
            "select TITLE, DETAIL FROM " + schema_prefix + "GUIDANCE WHERE COMPONENT_ID=? ORDER BY GUIDANCE_ORDER";
        sel_displays_by_id = 
            "select TITLE, DETAIL FROM " + schema_prefix + "DISPLAY WHERE COMPONENT_ID=? ORDER BY DISPLAY_ORDER";
        sel_commands_by_id = 
            "select TITLE, DETAIL FROM " + schema_prefix + "COMMAND WHERE COMPONENT_ID=? ORDER BY COMMAND_ORDER";
        // Note how components are ordered by ID, assuming they are originally
        // added in some divine order, but PVs tend to get ordered by name
        sel_items_by_parent =
            "SELECT COMPONENT_ID, NAME, CONFIG_TIME" +
            " FROM " + schema_prefix + "ALARM_TREE WHERE PARENT_CMPNT_ID=? ORDER BY COMPONENT_ID";
        sel_last_item_id =
            "SELECT MAX(COMPONENT_ID) FROM " + schema_prefix + "ALARM_TREE";
        
        // Database might not provide a default config time, so include
        // that in the INSERT statement
        final String now = rdb.getDialect() == Dialect.Oracle
                         ? "SYSDATE" : "NOW()";
        insert_item =
            "INSERT INTO " + schema_prefix +
            "ALARM_TREE(COMPONENT_ID, PARENT_CMPNT_ID, NAME, CONFIG_TIME)" +
            " VALUES (?,?,?," + now + ")";
        
        delete_guidance_by_id = 
            "DELETE FROM " + schema_prefix + "GUIDANCE WHERE COMPONENT_ID=?";
        insert_guidance =
            "INSERT INTO " + schema_prefix + "GUIDANCE(COMPONENT_ID, GUIDANCE_ORDER, TITLE, DETAIL) VALUES(?,?,?,?)";
        delete_displays_by_id = 
            "DELETE FROM " + schema_prefix + "DISPLAY WHERE COMPONENT_ID=?";
        insert_display =
            "INSERT INTO " + schema_prefix + "DISPLAY(COMPONENT_ID, DISPLAY_ORDER, TITLE, DETAIL) VALUES(?,?,?,?)";
        delete_commands_by_id = 
            "DELETE FROM " + schema_prefix + "COMMAND WHERE COMPONENT_ID=?";
        insert_command =
            "INSERT INTO " + schema_prefix + "COMMAND(COMPONENT_ID, COMMAND_ORDER, TITLE, DETAIL) VALUES(?,?,?,?)";
            
        update_item_config_time =
            "UPDATE " + schema_prefix + "ALARM_TREE SET CONFIG_TIME=? WHERE COMPONENT_ID=?";
        
        delete_component_by_id = "DELETE FROM " + schema_prefix + "ALARM_TREE WHERE COMPONENT_ID = ?";
        
        sel_pvs_by_parent =
            //        1               2       3        4              5                 6            7        8
            "SELECT p.COMPONENT_ID, t.NAME, p.DESCR, p.ENABLED_IND, p.ANNUNCIATE_IND, p.LATCH_IND, p.DELAY, p.DELAY_COUNT," +
            //  9         10                 11               12             13           14          15            16
            " p.FILTER, p.CUR_SEVERITY_ID, p.CUR_STATUS_ID, p.SEVERITY_ID, p.STATUS_ID, p.PV_VALUE, p.ALARM_TIME, t.CONFIG_TIME" +
            " FROM " + schema_prefix + "PV p" +
            " JOIN " + schema_prefix + "ALARM_TREE t ON p.COMPONENT_ID = t.COMPONENT_ID " +
            " WHERE t.PARENT_CMPNT_ID=? " +
            " ORDER BY NAME";
           
        sel_pv_by_id =
            "SELECT DESCR, ENABLED_IND, ANNUNCIATE_IND, LATCH_IND, DELAY, DELAY_COUNT, FILTER FROM " + schema_prefix + "PV WHERE COMPONENT_ID=?";
        insert_pv =
            "INSERT INTO " + schema_prefix + "PV(COMPONENT_ID, DESCR, ANNUNCIATE_IND, LATCH_IND,ENABLED_IND) VALUES (?,?,?,?,1)";
       
        update_pv_config =
            "UPDATE " + schema_prefix + "PV SET DESCR=?,ENABLED_IND=?,ANNUNCIATE_IND=?,LATCH_IND=?, DELAY=?,DELAY_COUNT=?,FILTER=? WHERE COMPONENT_ID=?";
        update_pv_state =
            "UPDATE " + schema_prefix + "PV SET CUR_SEVERITY_ID=?,CUR_STATUS_ID=?,SEVERITY_ID=?,STATUS_ID=?,PV_VALUE=?,ALARM_TIME=?  WHERE COMPONENT_ID=?";
        update_pv_enablement =
            "UPDATE " + schema_prefix + "PV SET ENABLED_IND=?  WHERE COMPONENT_ID=?";
        delete_pv_by_id = "DELETE FROM " + schema_prefix + "PV WHERE COMPONENT_ID = ?";
        rename_item = "UPDATE " + schema_prefix + "ALARM_TREE SET NAME=? WHERE COMPONENT_ID=?";
        move_item = "UPDATE " + schema_prefix + "ALARM_TREE SET PARENT_CMPNT_ID=? WHERE COMPONENT_ID=?";
        
        sel_severity =
            "SELECT SEVERITY_ID FROM " + schema_prefix + "SEVERITY WHERE NAME=?";
        sel_last_severity =
            "SELECT MAX(SEVERITY_ID) FROM " + schema_prefix + "SEVERITY";
        insert_severity =
            "INSERT INTO " + schema_prefix + "SEVERITY(SEVERITY_ID, NAME) VALUES (?,?)";
    }   
}