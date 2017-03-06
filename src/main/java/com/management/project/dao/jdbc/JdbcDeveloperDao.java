package com.management.project.dao.jdbc;

import com.management.project.connections.ConnectionDB;
import com.management.project.dao.CompanyDAO;
import com.management.project.dao.DeveloperDAO;
import com.management.project.dao.ProjectDAO;
import com.management.project.factory.FactoryDao;
import com.management.project.models.Developer;
import com.management.project.models.Skill;

import java.sql.*;
import java.util.*;

/**
 * @author Вадим
 */
public class JdbcDeveloperDao implements DeveloperDAO {

    private final static String SAVE = "INSERT INTO developers(name, company_id, project_id,salary) VALUES(?,?,?,?)";
    private static final String SAVE_SKILLS = "INSERT developers_skills VALUES (?,?)";
    private final static String FIND_BY_ID = "SELECT * FROM developers WHERE ID = ?";
    private final static String UPDATE = "UPDATE developers SET name = ?, company_id = ?,project_id = ?,salary =? WHERE ID = ?";
    private final static String DELETE = "DELETE FROM developers WHERE ID = ?";
    private final static String DELETE_SKILLS = "DELETE FROM developers_skills WHERE developer_id = ?";
    private final static String FIND_ALL = "SELECT * FROM developers";
    private final static String FIND_BY_NAME = "SELECT * FROM developers WHERE NAME = ?";
    private final static String GET_LAST_INSERTED = "SELECT LAST_INSERT_ID()";
    private final static String GET_SKILLS = "SELECT * FROM skills " +
            "JOIN developers_skills ON skills.ID = developers_skills.skill_id " +
            "JOIN developers ON developers_skills.developer_id = developers.id " +
            "WHERE developers.id=?";

    /**
     * an instance of JdbcCompanyDAO
     */
    private CompanyDAO companyDAO;

    /**
     * an instance of JdbcProjectDAO
     */
    private ProjectDAO projectDAO;

    /**
     * a connection to database
     */
    private ConnectionDB connectionDB;

    /**
     * * Constructor.
     *
     * @param connectionDB a connection to database
     */
    public JdbcDeveloperDao(ConnectionDB connectionDB) throws SQLException {
        this.connectionDB = connectionDB;
        companyDAO = FactoryDao.getCompanyDAO();
        projectDAO = FactoryDao.getProjectDAO();
    }

    /**
     * Method finds a developer in database by name of developer
     *
     * @param name a name of a developer
     * @return a developer with entered name
     * or null if developer with this name does not exist
     */
    @Override
    public Developer findDeveloperByName(String name) {
        try (Connection connection = connectionDB.getConnection()){
            Developer developer;
            try(PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_NAME)) {
                preparedStatement.setString(1, name);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        return null;
                    }
                    developer = createDeveloper(resultSet);
                }
            }
            HashSet<Skill> skills = createSkills(connection, developer);
            developer.setSkills(skills);
            return developer;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Method saves a new developer in the database
     *
     * @param obj a developer, which must be save in the database
     * @return developer`s id if the developer was add to database successfully
     */
    @Override
    public Long save(Developer obj) {
        Connection connection = null;
        try { connection = connectionDB.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(SAVE)){
                statement.setString(1,obj.getName());
                statement.setLong(2,obj.getCompany().getId());
                statement.setLong(3,obj.getProject().getId());
                statement.setInt(4,obj.getSalary());
                statement.executeUpdate();
            }
            long id = 0;
            try (Statement statement = connection.createStatement()){
                ResultSet resultSet = statement.executeQuery(GET_LAST_INSERTED);
                resultSet.next();
                id = resultSet.getLong(1);
            }
            try (PreparedStatement statement = connection.prepareStatement(SAVE_SKILLS)){
                for(Skill skill: obj.getSkills()){
                    statement.setLong(1,id);
                    statement.setLong(2,skill.getId());
                    statement.addBatch();
                }
                statement.executeBatch();
            }
            connection.commit();
            return id;

        } catch (Exception e) {
            try {
                if (connection!=null) {
                    connection.rollback();
                }
            } catch (SQLException e1) {
                throw new RuntimeException(e1);
            }
            throw new RuntimeException(e);
        } finally {
            if (connection!=null){
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    /**
     * Method finds a developer in database by id
     *
     * @param aLong an id of a developer
     * @return a developer with entered id
     * or null if developer with this id does not exist
     */
    @Override
    public Developer findById(Long aLong) {
        try (Connection connection = connectionDB.getConnection()){
            Developer developer;
            try(PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID)) {
                preparedStatement.setLong(1, aLong);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        return null;
                    }
                    developer = createDeveloper(resultSet);
                }
            }
            HashSet<Skill> skills = createSkills(connection, developer);
            developer.setSkills(skills);
            return developer;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method updates a developer in the database
     *
     * @param obj new developer
     */
    @Override
    public void update(Developer obj) {
        Connection connection = null;
        try { connection = connectionDB.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(UPDATE)){
                statement.setString(1,obj.getName());
                statement.setLong(2,obj.getCompany().getId());
                statement.setLong(3,obj.getProject().getId());
                statement.setInt(4,obj.getSalary());
                statement.executeUpdate();
            }

            try (PreparedStatement statement = connection.prepareStatement(DELETE_SKILLS)){
                statement.setLong(1,obj.getId());
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement(SAVE_SKILLS)){
                for(Skill skill: obj.getSkills()){
                    statement.setLong(1,obj.getId());
                    statement.setLong(2,skill.getId());
                    statement.addBatch();
                }
                statement.executeBatch();
            }
            connection.commit();

        } catch (Exception e) {
            try {
                if (connection!=null) {
                    connection.rollback();
                }
            } catch (SQLException e1) {
                throw new RuntimeException(e1);
            }
            throw new RuntimeException(e);
        } finally {
            if (connection!=null){
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    /**
     * Method removes a developer from database
     *
     * @param obj developer which must be removed
     */
    @Override
    public void delete(Developer obj) {
        Connection connection = null;
        try { connection = connectionDB.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(DELETE)){
                statement.setLong(1,obj.getCompany().getId());
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement(DELETE_SKILLS)){
                statement.setLong(1,obj.getId());
                statement.executeUpdate();
            }
            connection.commit();

        } catch (Exception e) {
            try {
                if (connection!=null) {
                    connection.rollback();
                }
            } catch (SQLException e1) {
                throw new RuntimeException(e1);
            }
            throw new RuntimeException(e);
        } finally {
            if (connection!=null){
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    /**
     * Method returns all developers from the database
     *
     * @return list of all developers from the database
     */
    @Override
    public List<Developer> findAll() {
        List<Developer> developers = new ArrayList<>();
        try(Connection connection = connectionDB.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(FIND_ALL)){
            while (resultSet.next()){
                Developer developer = createDeveloper(resultSet);
                HashSet<Skill> skills = createSkills(connection,developer);
                developer.setSkills(skills);
                developers.add(developer);
            }
            return developers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method creates new Set of skills for developer
     *
     * @param connection a current connection
     * @param developer a developer, which skills must de created
     * @return a set of skills, which was created
     * @throws SQLException
     */
    private HashSet<Skill> createSkills(Connection connection, Developer developer) throws SQLException {
        HashSet<Skill> skills;
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_SKILLS)){
            preparedStatement.setLong(1, developer.getId());
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                skills = new HashSet<>();
                while (resultSet.next()){
                    Skill skill = new Skill();
                    skill.setId(resultSet.getLong("id"));
                    skill.setName(resultSet.getString("skill"));
                    skills.add(skill);
                }
            }
        }
        return skills;
    }

    /**
     * Method creates new  developer
     *
     * @param resultSet resultSet a set of result from statement query
     * @return a developer, which was created on the basics of resultSet
     * @throws SQLException
     */
    private Developer createDeveloper(ResultSet resultSet) throws SQLException {
        Developer developer;
        developer = new Developer();
        developer.setId(resultSet.getLong("id"));
        developer.setName(resultSet.getString("name"));
        developer.setSalary(resultSet.getInt("salary"));
        developer.setCompany(companyDAO.findById(resultSet.getLong("company_id")));
        developer.setProject(projectDAO.findById(resultSet.getLong("project_id")));
        return developer;
    }

}
