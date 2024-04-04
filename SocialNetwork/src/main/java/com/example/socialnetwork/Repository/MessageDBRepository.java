package com.example.socialnetwork.Repository;

import com.example.socialnetwork.Domain.Message;
import com.example.socialnetwork.Validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class MessageDBRepository implements Repository<Long, Message> {

    private String url;
    private String username;
    private String password;

    private Validator<Message> validator;


    public MessageDBRepository(String url, String username, String password, Validator<Message> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator=validator;
    }

    @Override
    public Optional<Message> findOne(Long longID) {
        Message message=null;
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Messages WHERE \"MessageID\" = ?");

        ) {
            statement.setInt(1, Math.toIntExact(longID));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String text = resultSet.getString("text");
                Timestamp timestamp = resultSet.getTimestamp("date");
                LocalDateTime date= LocalDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.ofHours(0));
                Long fromID = resultSet.getLong("fromID");
                Long reply = resultSet.getLong("reply");


                PreparedStatement statement2 = connection.prepareStatement("SELECT * FROM recipients WHERE \"MessageID\" = ?");
                statement2.setLong(1, longID);
                resultSet = statement2.executeQuery();

                ArrayList<Long> recipients = new ArrayList<>();
                while(resultSet.next())
                {
                    recipients.add(resultSet.getLong("toID"));
                }

                message = new Message(fromID, recipients, text, date,reply);
                message.setId(longID);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(message);
    }

    @Override
    public Iterable<Message> findAll() {
        Set<Message> messages = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from Messages");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next())
            {
                Long idMesaj = resultSet.getLong("MessageID");
                String text = resultSet.getString("text");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Long idFrom = resultSet.getLong("fromID");
                Long reply= resultSet.getLong("reply");

                PreparedStatement statement2 = connection.prepareStatement("SELECT * FROM recipients WHERE \"MessageID\" = ?");
                statement2.setLong(1, idMesaj);
                ResultSet resultSet2 = statement2.executeQuery();

                ArrayList<Long> recipients = new ArrayList<>();
                while(resultSet2.next())
                {
                    recipients.add(resultSet2.getLong("toID"));
                }

                Message message = new Message(idFrom, recipients, text, date,reply);
                message.setId(idMesaj);
                messages.add(message);

            }
            return new ArrayList<>(messages);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Message> save(Message entity) {

        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        validator.validate(entity);

        String query;
        if(entity.getReply()==null)
         query= "INSERT INTO Messages(\"text\",\"date\",\"fromID\") VALUES (?,?,?)";
        else{
            query= "INSERT INTO Messages(\"text\",\"date\",\"fromID\",\"reply\") VALUES (?,?,?,?)";
        }
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, entity.getMessage());
            Timestamp friendsFrom = Timestamp.valueOf(entity.getDate());
            statement.setTimestamp(2, friendsFrom);
            statement.setLong(3, entity.getFrom());
            if(entity.getReply()!=null)
                statement.setLong(4, entity.getReply());
            statement.executeUpdate();

            PreparedStatement statement3 = connection.prepareStatement("SELECT CURRVAL(pg_get_serial_sequence('Messages', 'MessageID'));");
            ResultSet resultSet = statement3.executeQuery();
            if(resultSet.next()) {

                Long messageID= resultSet.getLong("currval");
                entity.setId(messageID);

                List<Long> recipients = entity.getTo();
                PreparedStatement statement2 = connection.prepareStatement("INSERT INTO recipients(\"toID\", \"MessageID\") VALUES (?, ?)");
                for (Long recipient : recipients) {
                    statement2.setLong(1, recipient);
                    statement2.setLong(2, entity.getId());
                    statement2.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.ofNullable(entity);
    }

    @Override
    public Optional<Message> delete(Long aLong)
    {
        return null;
    }


    @Override
    public Optional<Message> update(Message entity)
    {
        return null;
    }
}
