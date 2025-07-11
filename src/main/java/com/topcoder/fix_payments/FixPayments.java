package com.topcoder.fix_payments;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FixPayments {

    private static void fixProject(int challengeId){
        String insertWinner="insert into tcs_catalog:project_info \n" + //
                        "values (\n" + //
                        "        " + challengeId + ", \n" + //
                        "        23, \n" + //
                        "        (select user_id from tcs_catalog:project_result where project_id=" + challengeId + "), \n" + //
                        "        151743, \n" + //
                        "        current, \n" + //
                        "        151743, \n" + //
                        "        current\n" + //
                        ")";
        System.out.println("SQL: " + insertWinner);
        try{
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(insertWinner);
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }

    }

    public static long getId(String sequence){
        long result = -1;
        try{
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT next_block_start FROM id_sequences WHERE name = \"" + sequence + "\"");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                result = rs.getLong("next_block_start");
            }
            ps = conn.prepareStatement("UPDATE id_sequences SET next_block_start=? WHERE name = \"" + sequence+ "\"");
            ps.setLong(1, result+1);
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        System.out.println("Next " + sequence + ": " + result);
        return result;
    }
    public static void main(String[] args) {

		System.out.println("Starting to fix payments");

        final String MISSING_WINNER="select p.project_id\n" + //
                        "from tcs_catalog:project p\n" + //
                        "where p.project_status_id=7 and p.project_category_id=38\n" + //
                        "and p.project_id not in (select project_id from tcs_catalog:project_info where project_info_type_id=23)\n" + //
                        "and p.create_date>'2025-01-01 00:00:00'\n";
        try{
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(MISSING_WINNER);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int challengeId=rs.getInt("project_id");
                System.out.println("Fixing: " + challengeId);
                fixProject(challengeId);
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        final String INSERT_PAYMENT = "insert into informixoltp:payment(\n" + //
                        "               payment_id,\n" + //
                        "               user_id,\n" + //
                        "               most_recent_detail_id,\n" + //
                        "               referral_payment_id,\n" + //
                        "               pay_referrer,\n" + //
                        "               create_date,\n" + //
                        "               modify_date,\n" + //
                        "               has_global_ad\n" + //
                        "            ) values (\n" + //
                        "               ?,\n" + //
                        "               ?,\n" + //
                        "               ?,\n" + //
                        "               null,\n" + //
                        "               null,\n" + //
                        "               current,\n" + //
                        "               current,\n" + //
                        "               'f'\n" + //
                        "            )";

        final String INSERT_PAYMENT_DETAILS = "insert into informixoltp:payment_detail(\n" + //
                        "        payment_detail_id, \n" + //
                        "        net_amount,\n" + //
                        "        date_paid,\n" + //
                        "        gross_amount,\n" + //
                        "        payment_status_id,\n" + //
                        "        payment_address_id,\n" + //
                        "        modification_rationale_id,\n" + //
                        "        payment_desc,\n" + //
                        "        payment_type_id,\n" + //
                        "        date_modified,\n" + //
                        "        date_due,\n" + //
                        "        payment_method_id,\n" + //
                        "        client,\n" + //
                        "        algorithm_round_id,\n" + //
                        "        algorithm_problem_id,\n" + //
                        "        component_contest_id,\n" + //
                        "        component_project_id,\n" + //
                        "        studio_contest_id,\n" + //
                        "        digital_run_stage_id,\n" + //
                        "        digital_run_season_id,\n" + //
                        "        parent_payment_id,\n" + //
                        "        create_date,\n" + //
                        "        charity_ind,\n" + //
                        "        total_amount,\n" + //
                        "        installment_number,\n" + //
                        "        digital_run_track_id,\n" + //
                        "        jira_issue_id,\n" + //
                        "        create_user,\n" + //
                        "        cockpit_project_id\n" + //
                        "        ) values (\n" + //
                        "                ?,\n" + //
                        "                ?,\n" + //
                        "                null,\n" + //
                        "                ?,\n" + //
                        "                55,\n" + //
                        "                null,\n" + //
                        "                1,\n" + //
                        "                ?,\n" + //
                        "                65,\n" + //
                        "                current,\n" + //
                        "                extend(current) + 30 UNITS DAY,\n" + //
                        "                1,\n" + //
                        "                null,\n" + //
                        "                null,\n" + //
                        "                null,\n" + //
                        "                null,\n" + //
                        "                ?,\n" + //
                        "                null,\n" + //
                        "                null,\n" + //
                        "                null,\n" + //
                        "                null,\n" + //
                        "                current, \n" + //
                        "                0,\n" + //
                        "                ?,\n" + //
                        "                1,\n" + //
                        "                null,\n" + //
                        "                null,\n" + //
                        "                null,\n" + //
                        "                null\n" + //
                        "       )";
                 
        final String UPDATE_PROJECT_PAYMENT = "update tcs_catalog:project_payment set pacts_payment_id=? where project_payment_id=?";
                        
        final String SELECT_PAYMENTS = "select pp.project_payment_id, \n" + //
                                    "        pp.amount, \n" + //
                                    "        pp.submission_id, \n" + //
                                    "        r.user_id, \n" + //
                                    "        r.project_id as challenge_id, \n" + //
                                    "        pi.value as challenge_name\n" + //
                                    "from tcs_catalog:project_payment pp\n" + //
                                    "inner join tcs_catalog:resource r on pp.resource_id = r.resource_id\n" + //
                                    "inner join tcs_catalog:project_info pi on r.project_id=pi.project_id  \n" + //
                                    "where pp.amount>0 and pp.pacts_payment_id is null and pp.project_payment_type_id=1 and pp.create_date>\"2024-11-01 00:00:00\"\n" + //
                                    "and pi.project_info_type_id=6";
        try{
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(SELECT_PAYMENTS);
            ResultSet rs = ps.executeQuery();
            /* For each missing payment, we:
            1. Insert a payment detail record
            2. Insert a payment record associated with the payment detail
            3. Update the project payment to refer to the payment record
            */
            while(rs.next()){
                System.out.println("Inserting payment detail record: " + rs.getInt("challenge_id") + " " + rs.getDouble("amount") + " " + rs.getString("challenge_name") );

                long detailId=getId("PAYMENT_DETAIL_SEQ");

                PreparedStatement insertPaymentDetail = conn.prepareStatement(INSERT_PAYMENT_DETAILS);

                insertPaymentDetail.setLong(1, detailId);
                insertPaymentDetail.setDouble(2, rs.getDouble("amount"));
                insertPaymentDetail.setDouble(3, rs.getDouble("amount"));
                insertPaymentDetail.setString(4, rs.getString("challenge_name"));
                insertPaymentDetail.setInt(5, rs.getInt("challenge_id"));
                insertPaymentDetail.setDouble(6, rs.getDouble("amount"));
                insertPaymentDetail.executeUpdate();
                
                System.out.println("Inserting payment record: " + rs.getInt("user_id"));
                long paymentId=getId("PAYMENT_SEQ");
                PreparedStatement insertPayment = conn.prepareStatement(INSERT_PAYMENT);
                insertPayment.setLong(1, paymentId);
                insertPayment.setInt(2, rs.getInt("user_id"));
                insertPayment.setLong(3, detailId);
                insertPayment.executeUpdate();

                System.out.println("Updating project payment: " + rs.getInt("project_payment_id"));
                PreparedStatement updateProjectPayment = conn.prepareStatement(UPDATE_PROJECT_PAYMENT);
                updateProjectPayment.setLong(1, paymentId);
                updateProjectPayment.setInt(2, rs.getInt("project_payment_id"));
                updateProjectPayment.executeUpdate();
            }

        
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

    private static String dbUrl = "<ADD CONNECTION STRING>";
    
    private static Connection getConnection()
    {
            Connection connection = null;
            try {
                connection = DriverManager.getConnection(dbUrl);
            } catch (SQLException e) {
                System.out.println("Could not connect to DB: " + e.getMessage());
            }
            return connection;
    }

    private static int outputSize(Connection connection, String query, String table ){
        try{
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            int result = 0;
            while(rs.next()){
            //     System.out.println("Writing " + result + " " + table + " records");
                result = rs.getInt("count");
            }
            return result;
        }
        catch(SQLException e){
            System.out.println("Size Exception " + e.getMessage());
        }
        return -1;
    }
}
