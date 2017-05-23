package com.tools.base;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.firebirdsql.management.FBManager;
import com.tools.conexao.FirebirdConnection;
import com.tools.conexao.ConfigFirebird;



public class Estrutura {
	

	private void getAndExportGenerators(){
		
		try {
		
			FirebirdConnection conEco = new FirebirdConnection(ConfigFirebird.caminhoEco);
			FirebirdConnection conDestino = new FirebirdConnection(ConfigFirebird.caminhoDestino);
			
			ResultSet rs = conEco.stm.executeQuery("select rdb$generator_name as generator from rdb$generators where rdb$system_flag is distinct from 1");
			
			HashMap<String, Integer> lstGenerators = new HashMap<String, Integer>();
			
			
			while(rs.next()){
				lstGenerators.put(rs.getString("generator").trim(), 0);
			}
			
			rs.close();
			
			List<String> lstCmdGenerator = new ArrayList<String>();
			
			for (Map.Entry<String, Integer> entry : lstGenerators.entrySet()  ) {
				rs = conEco.stm.executeQuery("select gen_id("+entry.getKey()+", 0) from rdb$database");
				rs.next();
				System.out.println( entry.getKey() + " : " + rs.getInt("gen_id"));
				entry.setValue(rs.getInt("gen_id"));				
				
				lstCmdGenerator.add("CREATE SEQUENCE " + entry.getKey());
				lstCmdGenerator.add("ALTER SEQUENCE "+entry.getKey()+" RESTART WITH "+entry.getValue());
				
			}
			
			for (String str : lstCmdGenerator) {
				conDestino.con.setAutoCommit(false);
				conDestino.stm.execute(str);
				conDestino.con.commit();
			}
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}
	
	
	
	private void corrigeCampos(){
		try {		
			FirebirdConnection conOrigem = new FirebirdConnection(ConfigFirebird.caminhoEco);
			
			String sql = " EXECUTE BLOCK "
					+" AS "
					+"   DECLARE VARIABLE RELNAME VARCHAR(31); "
					+"   DECLARE VARIABLE FDNAME VARCHAR(31); "
					+" BEGIN "
					+"   FOR SELECT RF.RDB$RELATION_NAME RELNAME, RF.RDB$FIELD_NAME FDNAME "
					+"   FROM RDB$RELATION_FIELDS RF "
					+"   INNER JOIN RDB$RELATIONS REL ON (REL.RDB$RELATION_NAME = RF.RDB$RELATION_NAME) "
					+"   INNER JOIN RDB$FIELDS DOM ON (RF.RDB$FIELD_SOURCE = DOM.RDB$FIELD_NAME) "
					+"   WHERE (REL.RDB$VIEW_BLR IS NULL) AND (DOM.RDB$FIELD_TYPE IN (8, 16)) AND (DOM.RDB$FIELD_SCALE < 0) "
					+"   INTO :RELNAME, :FDNAME DO "
					+"   BEGIN "
					+"     IN AUTONOMOUS TRANSACTION DO "
					+"     BEGIN "
					+"       EXECUTE STATEMENT 'UPDATE ' || RELNAME || ' SET ' || FDNAME || ' = 0 WHERE ' || FDNAME || ' < -100000'; "
					+"       WHEN ANY DO "
					+"       BEGIN "
					+"       END "
					+"     END "
					+"   END "
					+" END ";
			
			System.out.println("Corrigindo campos...");
			if (conOrigem.stm.execute(sql)){
				System.out.println("Processo Concluido...");
			}
		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		
	}
	
	private void criaEstrutura(){
		
			
		
//		CRIA A BASE DE DADOS DESTINO
		try {
			FBManager manager = new FBManager();
			manager.start();			
			manager.createDatabase(ConfigFirebird.caminhoDestino, "SYSDBA", "masterkey");
			manager.stop();
		
		
			FirebirdConnection conOrigem = new FirebirdConnection(ConfigFirebird.caminhoOrigem);		
			FirebirdConnection conDestino = new FirebirdConnection(ConfigFirebird.caminhoDestino);
			conDestino.con.setAutoCommit(false);
			
			
			ResultSet rsTabelas = conOrigem.stm.executeQuery(
					"select "
					+ "RDB$RELATION_NAME, "
					+ "RDB$SYSTEM_FLAG, "
					+ "RDB$EXTERNAL_FILE, "
					+ "RDB$DESCRIPTION "
					+ "from "
					+ "RDB$RELATIONS "
					+ "where "
					+ "(RDB$SYSTEM_FLAG <> 1) and "
					+ "(RDB$VIEW_BLR is null) /* sem as view */ "
					+ "and "
					+ "not(RDB$RELATION_NAME like 'IBE%')");
			
			List<String> lstTabelas = new ArrayList<String>(); 
			
			while(rsTabelas.next()){
				System.out.println(rsTabelas.getString("RDB$RELATION_NAME"));
				lstTabelas.add(rsTabelas.getString("RDB$RELATION_NAME"));			
			}
			
			for (String tabela : lstTabelas) {
				ResultSet rsFields = conOrigem.stm.executeQuery(
						  "SELECT rf.rdb$field_name          AS fld_name, "
						+ "       rf.rdb$field_source        AS fld_domain, "
						+ "       rf.rdb$null_flag           AS fld_null_flag, "
						+ "       rf.rdb$default_source      AS fld_default, "
						+ "       rf.rdb$description         AS fld_description, "
						+ "		  f.rdb$field_precision     as dom_precision,	"
						+ "       f.rdb$field_type           AS dom_type, "
						+ "       f.rdb$field_length         AS dom_length, "
						+ "       f.rdb$field_sub_type       AS dom_subtype, "
						+ "       f.rdb$field_scale          AS dom_scale, "
						+ "       f.rdb$null_flag            AS dom_null_flag, "
						+ "       f.rdb$character_length     AS dom_charlen, "
						+ "       f.rdb$segment_length       AS dom_seglen, "
						+ "       f.rdb$system_flag          AS dom_system_flag, "
						+ "       f.rdb$computed_source      AS dom_computedby, "
						+ "       f.rdb$default_source       AS dom_default, "
						+ "       f.rdb$dimensions           AS dom_dims, "
						+ "       f.rdb$description          AS dom_description, "
						+ "       ch.rdb$character_set_name  AS dom_charset, "
						+ "       ch.rdb$bytes_per_character AS charset_bytes, "
						+ "       dco.rdb$collation_name     AS dom_collation, "
						+ "       fco.rdb$collation_name     AS fld_collation "
						+ "FROM   rdb$relation_fields rf "
						+ "       LEFT JOIN rdb$fields f "
						+ "              ON rf.rdb$field_source = f.rdb$field_name "
						+ "       LEFT JOIN rdb$character_sets ch "
						+ "              ON f.rdb$character_set_id = ch.rdb$character_set_id "
						+ "       LEFT JOIN rdb$collations dco "
						+ "              ON ( ( f.rdb$collation_id = dco.rdb$collation_id ) "
						+ "                   AND ( f.rdb$character_set_id = dco.rdb$character_set_id ) ) "
						+ "       LEFT JOIN rdb$collations fco "
						+ "              ON ( ( rf.rdb$collation_id = fco.rdb$collation_id ) "
						+ "                   AND ( f.rdb$character_set_id = fco.rdb$character_set_id ) ) "
						+ "WHERE  rf.rdb$relation_name = '"+tabela+"' "
						+ "ORDER  BY rf.rdb$field_position");
				
				StringBuilder sfilds = new StringBuilder();
				
				
				while(rsFields.next()){
					
					if(sfilds.length() > 0)
						sfilds.append(", ");
	
					sfilds.append("\"" + rsFields.getString("fld_name").trim() + "\"");
					
					if(rsFields.getInt("dom_type") == 7){
						sfilds.append(" SMALLINT");
					} else if (rsFields.getInt("dom_type") == 8) {
						sfilds.append(" INTEGER");
					} else if (rsFields.getInt("dom_type") == 10) {
						sfilds.append(" FLOAT");
					} else if (rsFields.getInt("dom_type") == 12) {
						sfilds.append(" DATE");
					} else if (rsFields.getInt("dom_type") == 13) {
						sfilds.append(" TIME");
					} else if (rsFields.getInt("dom_type") == 14) {
						sfilds.append(" CHAR(" + rsFields.getInt("dom_length") + ")");
					} else if (rsFields.getInt("dom_type") == 16) {
						
						if(rsFields.getInt("dom_subtype") == 0)					
							sfilds.append(" BIGINT");
						else if(rsFields.getInt("dom_subtype") == 1)
							sfilds.append(" NUMERIC(" + rsFields.getInt("dom_precision") + "," + (rsFields.getInt("dom_scale")*-1) + ")");
						else if(rsFields.getInt("dom_subtype") == 2)
							sfilds.append(" DECIMAL(" + rsFields.getInt("dom_precision") + "," + (rsFields.getInt("dom_scale")*-1) + ")");					
						
						
					} else if (rsFields.getInt("dom_type") == 27) {
						sfilds.append(" DOUBLE PRECISION");
					} else if (rsFields.getInt("dom_type") == 35) {
						sfilds.append(" TIMESTAMP");
					} else if (rsFields.getInt("dom_type") == 37) {
						sfilds.append(" VARCHAR(" + rsFields.getInt("dom_length") + ")");
					} else if (rsFields.getInt("dom_type") == 261) {
						sfilds.append(" BLOB");
						sfilds.append(" SUB_TYPE " + rsFields.getInt("dom_subtype"));
						sfilds.append(" SEGMENT SIZE " + rsFields.getInt("dom_seglen"));
					} else {
						continue;
					}
					
					
					if((rsFields.getString("dom_charset") != null) && (rsFields.getInt("dom_type") != 8))
						sfilds.append(" CHARACTER SET " + rsFields.getString("dom_charset").trim());	
					
					if(rsFields.getString("fld_default") != null)
						sfilds.append(" "+rsFields.getString("fld_default").trim());								
					
					
					if(rsFields.getInt("fld_null_flag") == 1)
						sfilds.append(" NOT NULL");	
					
					
						
				}
				
				String stringCreateTable = "CREATE TABLE " + tabela.trim() + " (" + sfilds.toString() +");";
				System.out.println(stringCreateTable);
				conDestino.stm.execute(stringCreateTable);
				conDestino.con.commit();
				
				
			}
			
			
			conDestino.con.close();
			conOrigem.con.close();
		
		
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


}
