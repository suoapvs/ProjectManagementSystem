package com.management.project.controllers;

import com.management.project.dao.GenericDAO;
import com.management.project.models.Customer;

import java.util.Scanner;

/**
 * DESCRIPTION - ???
 *
 * @author Slava Makhinich
 */
public class CustomerController extends AbstractModelController<Customer> {

    /**
     *
     * @param dao
     */
    public CustomerController(GenericDAO<Customer, Long> dao) {
        super(dao);
    }

    /**
     *
     * @return
     */
    @Override
    protected Customer getNewModel() {
        System.out.print("Input customer name: ");
        String customerName = new Scanner(System.in).nextLine();
        return new Customer(-100, customerName);
    }

    /**
     *
     */
    @Override
    protected void printMenu() {
        System.out.println();
        System.out.println("ACTIONS WITH CUSTOMERS:");
        super.printMenu();
    }
}
