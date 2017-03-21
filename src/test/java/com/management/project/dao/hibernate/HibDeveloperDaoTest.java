package com.management.project.dao.hibernate;

import com.management.project.factory.HibFactoryDao;
import com.management.project.models.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Slava Makhinich
 */
public class HibDeveloperDaoTest {

    private HibDeveloperDao hibDeveloperDao = (HibDeveloperDao) HibFactoryDao.getDeveloperDAO();
    private static HibCompanyDao companyDao = (HibCompanyDao) HibFactoryDao.getCompanyDAO();
    private static HibProjectDao projectDao = (HibProjectDao) HibFactoryDao.getProjectDAO();
    private static HibCustomerDao customerDao = (HibCustomerDao) HibFactoryDao.getCustomerDAO();
    private static HibSkillDao skillDao = (HibSkillDao) HibFactoryDao.getSkillDAO();
    private static HashSet<Skill> skills = new HashSet<>();
    private static Skill skill;
    private static Long skillId;
    private static Project project;
    private static Long projectId;
    private static Company company;
    private static Long companyId;
    private static Customer customer;
    private static Long customerId;

    @BeforeClass
    public static void createData () {
        skill = new Skill("TestSkill");
        skillId = skillDao.save(skill);
        skill.setId(skillId);
        skills.add(skill);
        customer = new Customer("TestCustomer");
        customerId = customerDao.save(customer);
        customer.setId(customerId);
        company = new Company(-1,"TestCompany");
        companyId = companyDao.save(company);
        company.setId(companyId);
        project = new Project(-1, "TestProject", 777, company, customer);
        projectId = projectDao.save(project);
        project.setId(projectId);
    }

    @AfterClass
    public static void cleanBase () {
        skillDao.delete(skill);
        projectDao.delete(project);
        customerDao.delete(customer);
        companyDao.delete(company);
    }

    @Test
    public void save() throws Exception {
        Developer developer = new Developer(-1, "for test save()", company, project, 999);
        hibDeveloperDao.save(developer);
        assertEquals(developer, hibDeveloperDao.findByName("for test save()"));
        hibDeveloperDao.delete(hibDeveloperDao.findByName("for test save()"));
        assertNull(hibDeveloperDao.findByName("for test save()"));
    }

    @Test
    public void findById() throws Exception {
        Developer developer = new Developer(-1, "for test findById()", company, project, 999);
        Long id = hibDeveloperDao.save(developer);
        assertEquals(developer, hibDeveloperDao.findById(id));
        hibDeveloperDao.delete(hibDeveloperDao.findById(id));
        assertNull(hibDeveloperDao.findById(id));
    }

    @Test
    public void update() throws Exception {
        Developer developer = new Developer(-1, "for test update()", company, project, 999, skills);
        Long id = hibDeveloperDao.save(developer);
        Developer developerFromDB = hibDeveloperDao.findById(id);
        developerFromDB.setName("after update");
        hibDeveloperDao.update(developerFromDB);
        assertEquals(developerFromDB, hibDeveloperDao.findByName("after update"));
        hibDeveloperDao.delete(hibDeveloperDao.findByName("after update"));

        Developer developer1 = new Developer(1111100, "not exist in base", company, project, 1000);
        hibDeveloperDao.update(developer1);
        assertNull(hibDeveloperDao.findByName("not exist in base"));
    }

    @Test
    public void delete() throws Exception {
        Developer developer = new Developer(-1, "for test delete()", company, project, 999, skills);
        hibDeveloperDao.delete(developer);
        Long id = hibDeveloperDao.save(developer);
        assertNotNull(hibDeveloperDao.findByName("for test delete()"));
        hibDeveloperDao.delete(hibDeveloperDao.findByName("for test delete()"));
        assertNull(hibDeveloperDao.findByName("for test delete()"));
    }

    @Test
    public void findAll() throws Exception {
        Developer developer = new Developer(-1, "for test findAll()", company, project, 999);
        List<Developer> developers = hibDeveloperDao.findAll();
        Long id = hibDeveloperDao.save(developer);
        List<Developer> developersNew = hibDeveloperDao.findAll();
        assertTrue(developersNew.size() - developers.size() == 1);
        assertTrue(developersNew.contains(developer));
        hibDeveloperDao.delete(hibDeveloperDao.findById(id));
    }

    @Test
    public void findByName() throws Exception {
        Developer developer = new Developer(-1, "for test findByName()", company, project, 999);
        hibDeveloperDao.save(developer);
        assertEquals(developer, hibDeveloperDao.findByName("for test findByName()"));
        hibDeveloperDao.delete(hibDeveloperDao.findByName("for test findByName()"));
        assertNull(hibDeveloperDao.findByName("for test findByName()"));
    }

}