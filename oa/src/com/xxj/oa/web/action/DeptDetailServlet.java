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


public class DeptDetailServlet extends HttpServlet {
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
		out.print("<title>部门详情</title>");
	    out.print("</head>");
	    out.print("<body>");
		out.print("<h1 align='center'>部门详情</h1>");
        //连接数据库，显示对应部门的详情
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        //获取部门编号
        String deptno = request.getParameter("deptno");
        try {
            conn = DBUtil. getConnection();
            String sql = "select dname,loc from dept where deptno = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1,deptno);
            rs = ps.executeQuery();
            if(rs.next()) {
                String dname = rs.getString("dname");
                String loc = rs.getString("loc");
                out.print("<hr>");
                out.print("部门编号:"+deptno+"<br>");
                out.print("部门名称:"+dname+"<br>");
                out.print("部门位置:"+loc+"<br>");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        out.print("<input type='button' value='后退' onclick='window.history.back()'/>");
        out.print("</body>");
        out.print("</html>");
    }
}
