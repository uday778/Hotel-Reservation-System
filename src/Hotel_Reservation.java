import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;



public class Hotel_Reservation {
    private static final String url="jdbc:mysql://localhost:3306/hotel_db";
    private static final String userName="root";
    private static final String password="123456";



    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try{
            Connection connection=DriverManager.getConnection(url,userName,password);
            while(true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner scanner=new Scanner(System.in);
                System.out.println("1 . Reserve a room");
                System.out.println("2. view Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");
                int choice=scanner.nextInt();
                switch (choice){
                    case 1:
                        reserveRoom(connection,scanner);
                        break;
                    case 2:
                        viewReservation(connection);
                        break;
                    case 3:
                        getRoomNumber(connection,scanner);
                        break;
                    case 4:
                        updateReservation(connection,scanner);
                        break;
                    case 5:
                        deleteReservation(connection,scanner);
                        break;
                    case 0:
                        exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice, Try again");
                }
            }
        } catch (SQLException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    private static void reserveRoom(Connection connection,Scanner scanner){

        try{
            System.out.println("Enter Reservation ID:  ");
            int reservationId=scanner.nextInt();
            System.out.println("Enter guest name:  ");
            String guestName=scanner.next();
            scanner.nextLine();
            System.out.println("Enter room Number");
            int roomNumber=scanner.nextInt();
            System.out.println("Enter contact number: ");
            String contactNumber=scanner.next();

            String sql="INSERT INTO reservations(reservation_id, guest_name,room_number,contact_number)"+
                    "VALUES ("+reservationId+", '"+guestName+"',"+roomNumber+", '"+contactNumber+"')";
            try(Statement statement=connection.createStatement()){
                int affectedRows=statement.executeUpdate(sql);
                if(affectedRows>0){
                    System.out.println("reservation successfully!");
                }else{
                    System.out.println("Reservation failed.");
                }
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static void viewReservation(Connection connection)  {
        String sql=" SELECT reservation_id,guest_name,room_number,contact_number,reservation_date FROM reservations";

        try(Statement statement=connection.createStatement()){
            ResultSet resultSet=statement.executeQuery(sql);
            System.out.println("Current Reservations: ");
            System.out.println("+------------------------+-----------------------+----------------+--------------------------+------------+");
            System.out.println("|  Reservation ID  |      Guest    |     Room Number     |       contact Number      |    Reservation date|");
            System.out.println("+------------------------+-----------------------+----------------+--------------------------+------------+");

            while(resultSet.next()){
                int reservationId=resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber=resultSet.getInt("room_number");
                String contactNumber=resultSet.getString("contact_number");
                String reservationDate=resultSet.getTimestamp("reservation_date").toString();

                //format and display the reservation date in a table like format
                System.out.printf(" | %-14d | %-15s | %-13d | %-20s | %-19s |\n",
                        reservationId,guestName ,roomNumber,contactNumber,reservationDate);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private static void getRoomNumber(Connection connection,Scanner scanner){
        try{
            System.out.println("Enter Reservation Id:  ");
            int reservationId=scanner.nextInt();
            scanner.nextLine();
            System.out.println("Enter guest name:  ");
            String guestName=scanner.nextLine();

            String sql ="SELECT room_number FROM reservations "+
                    "WHERE reservation_id = "+reservationId+
                    " AND guest_name = '"+guestName+"'";
            try(Statement statement=connection.createStatement()){
                ResultSet resultSet =statement.executeQuery(sql);
                if(resultSet.next()){
                    int roomNumber= resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID "+ reservationId+
                            "and Guest"+guestName+"is: "+roomNumber);


                }else{
                    System.out.println("Reservation not found for the given ID and Guest name .");
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private  static  void updateReservation(Connection connection,Scanner scanner){
        try{
            System.out.println("Enter reservation ID to update:  ");
            int reservationId=scanner.nextInt();

            scanner.nextLine();//consume the new line character
            if(isreservationExists(connection, reservationId)){
                System.out.println("Reservation not found for the given ID. ");
                return;
            }
            System.out.println("Enter new Guest name :");
            String newGuestName=scanner.nextLine();
            System.out.println("Enter new room number: ");
            int newRoomNumber=scanner.nextInt();
            System.out.println("Enter new contact number:  ");
            String newContactNumber=scanner.next();

            String sql="UPDATE reservations SET guest_name= '"+
                    newGuestName+ " ',"+"room_number= "+newRoomNumber+", "+
                    "contact_number= ' "+newContactNumber+"'  "+
                    "WHERE reservation_id="+reservationId;

            try(Statement statement=connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);
                if(affectedRows>0){
                    System.out.println("Reservation updated successfully");
                }else{
                    System.out.println("Reservation update failed");
                }


            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }



    private static void deleteReservation(Connection connection,Scanner scanner){
        try{
            System.out.print("Enter reservation ID to delete :  ");
            int reservationId=scanner.nextInt();
            if(isreservationExists(connection, reservationId)){
                System.out.println("Reservation not found for the given ID");
                return;
            }
            String sql="DELETE FROM reservations WHERE reservation_id = "+reservationId;
            try(Statement statement=connection.createStatement()){
                int affectedRows=statement.executeUpdate(sql);
                if(affectedRows>0){
                    System.out.println("Reservation deleted successfully");
                }else{
                    System.out.println("Reservation deletion failed. ");
                }
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
    }



    private  static  boolean isreservationExists(Connection connection, int reservationId){
        try{
            String sql="SELECT reservation_id FROM reservations WHERE reservation_id = "+reservationId;
            try(Statement statement=connection.createStatement()) {
                ResultSet resultSet=statement.executeQuery(sql);
                return !resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static  void exit() throws InterruptedException {
        System.out.println("Exiting system");
        int i=5;
        while(i!=0){
            System.out.print(" . ");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("Thank you for using Hotel Reservation System   !!!");

    }





}