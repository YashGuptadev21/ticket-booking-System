package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtil;

import javax.swing.text.html.Option;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserBookingService {

    private User user;

    private List<User> userList;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String USERS_PATH = "app/src/java/ticketbooking/localDb/users.json";

    public UserBookingService(User user) throws IOException {
        this.user = user;
        loadUsers();
    }

    public UserBookingService() throws IOException {
        loadUsers();
    }

    public List<User> loadUsers() throws IOException {
        File users = new File(USERS_PATH);
        return userList = objectMapper.readValue(users, new TypeReference<List<User>>() {});
    }

    public Boolean loginUser(){
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(),user1.getHashedPassword());
        }).findFirst();
        return foundUser.isPresent();
    }

    public Boolean signUp(User user){
        try{
            userList.add(user);
            saveUserListToFile();
            return Boolean.TRUE;
        }catch (IOException ex){
            return Boolean.FALSE;
        }


        }
        private void saveUserListToFile() throws IOException {
        File file = new File(USERS_PATH);
        objectMapper.writeValue(file, userList);
    }

    public void fetchBooking(){
        user.ticketBooked();
    }

    public Boolean cancelBooking(String ticketId){
       Scanner s = new Scanner(System.in);
       System.out.println("Please enter the booking id you want to cancel: ");
       ticketId = s.next();

       if(ticketId == null || ticketId.isEmpty()){
           System.out.println("Booking id cannot be empty ");
           return Boolean.FALSE;
       }

       String finalTicketId1 = ticketId;
       boolean removed = user.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(finalTicketId1));

       String finalTicketId2 = ticketId;
       user.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(finalTicketId2));
       if(removed){
           System.out.println("Ticket with ID" + ticketId + "has been cancelled");
           return Boolean.TRUE;
       }else{
           System.out.println("No Ticket Found with Id" + ticketId);
           return Boolean.FALSE;
       }
    }

    public List<Train> getTrains(String source , String destination) {
      try{
          TrainService trainService = new TrainService();
           return trainService.searchTrains(source,destination);
      }catch(IOException ex){
        return new ArrayList<>();
      }
    }
    public List<List<Integer>> fetchSeats(Train train){
        return train.getSeats();
    }
    public Boolean bookTrainSeat(Train train, int row, int seat) {
        try{
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();
            if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
                if (seats.get(row).get(seat) == 0) {
                    seats.get(row).set(seat, 1);
                    train.setSeats(seats);
                    trainService.addTrain(train);
                    return true; // Booking successful
                } else {
                    return false; // Seat is already booked
                }
            } else {
                return false; // Invalid row or seat index
            }
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }
}
