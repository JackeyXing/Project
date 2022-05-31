package com.xxj.oa.web.action;

import com.xxj.oa.DButils.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeptDeleteServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //删除操作，删除之前需要提示用户
        String deptno = request.getParameter("deptno");
        Connection conn = null;
        PreparedStatement ps = null;
        //标志
        int count = 0;
        try {
            conn = DBUtil.getConnection();
            //开启事务
            conn.setAutoCommit(false);
            String sql = "delete from dept where deptno = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1,deptno);
            //返回值是影响了数据库表当中多少条记录
            count = ps.executeUpdate();
            //提交事务
            conn.commit();

        } catch (SQLException e) {
            if(conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            DBUtil.close(conn,ps,null);
        }

        //判断删除成功还是失败了
        if(count == 1) {
            //删除成功，跳转到闭门列表页面
            //需要执行另外一个servlet怎么办？转发或者重定向,这里用的转发
            request.getRequestDispatcher("/dept/list").forward(request,response);
        } else {
            request.getRequestDispatcher("/error.html").forward(request,response);
        }
    }
}
