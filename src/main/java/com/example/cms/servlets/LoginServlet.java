package com.example.cms.servlets;

import com.example.cms.dao.ProfessorDao;
import com.example.cms.misc.Helper;
import com.example.cms.models.Professor;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "LoginServlet", value = "/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String action =request.getServletPath();
        HttpSession session=request.getSession(false);
        if(session==null || Helper.checkProfessorFromCookies(session)==null){
            request.getRequestDispatcher("login.jsp").forward(request,response);
        }
        else{
        response.sendRedirect(request.getContextPath() + "/students");

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email=request.getParameter("email");
        String password=request.getParameter("password");
        String checked=request.getParameter("rememberCheck");

        try {
            if(validateInput(email,password) ) {


    //TODO:check if professor exist
            Professor p =null;
            ProfessorDao professorDao = new ProfessorDao();
            try {
             p =  professorDao.getProfessorInfoFromDB(email,password);
            } catch (SQLException e) {
                e.printStackTrace();
                request.setAttribute("error","Something went wrong!");
                request.getRequestDispatcher("login.jsp").forward(request,response);
            return;
            }
            if(p != null){
                HttpSession session=request.getSession();
                //TODO:selected professor
                session.setAttribute("current",p);
                session.setAttribute("email",p.getEmail());
                session.setAttribute("password",p.getPassword());
                response.sendRedirect(request.getContextPath() + "/students");
            }
            else{
                request.setAttribute("error","this account does not exist or the password is wrong!");
                request.getRequestDispatcher("login.jsp").forward(request,response);
            }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error",e.getMessage());
            request.getRequestDispatcher("login.jsp").forward(request,response);
        }
    }
    boolean validateInput(String email,String password) throws Exception{
        String emailRegex="^[A-Z0-9._%+-]+@[A-Z0-9.-]+.[A-Z]{2,6}$";
        if(email==null|| Helper.regexChecker(emailRegex, email)) throw new Exception("invalid Email address");
        if(password==null||!(password.length() > 7)) throw new Exception("invalid password");
        return true;
    }
}
