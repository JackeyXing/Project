package com.xxj.oa.web.action;

import com.xxj.oa.DButils.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeptEditServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String contextPath = request.getContextPath();
        out.print("<!DOCTYPE html>");
        out.print("<html>");
        out.print("<head>");
        out.print("<meta charset='utf-8'>");
        out.print("<title>修改部门</title>");
        out.print("</head>");
        out.print("<body>");
        out.print("<h1 align='center'>修改部门</h1>");
        out.print("<hr>");
        out.print("<form action='"+contextPath+"/dept/modify' method='POST' align='center'>");
        //获取部门编号
        String deptno = request.getParameter("deptno");
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "select dname,loc from dept where deptno = "+deptno+"";
            ps = conn.prepareStatement(sql);
            rs=ps.executeQuery();
            while(rs.next()) {
                String dname = rs.getString("dname");
                String loc = rs.getString("loc");
                out.print("部门编号<input type='text' name='deptno' readonly value='"+deptno+"'/><br>");
                out.print("部门名称<input type='text' name='dname' value='"+dname+"'/><br>");
                out.print("部门位置<input type='text' name='loc' value='"+loc+"'/><br>");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        out.print("<input type='submit' value='修改'/><br>");
        out.print("</form>");
        out.print("</body>");
        out.print("</html>");
    }

}
