package com.xxj.oa.web.action;

import com.xxj.oa.DButils.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//使用模板方法设计模式重写业务
@WebServlet({"/dept/list","/dept/modify", "/dept/detail", "/dept/delete", "/dept/edit", "/dept/save"})
public class DeptServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

            /**
             * switch (servletPath) {
             *             case "/dept/list":
             *                 doList(request,response);
             *             case "/dept/save":
             *                 doSave(request,response);
             *             case "/dept/edit":
             *                 doEdit(request,response);
             *             case "/dept/detail":
             *                 doDetail(request,response);
             *             case "/dept/delete":
             *                 doDel(request,response);
             *             case "/dept/modify":
             *                 doModify(request,response);
              */
            if ("/dept/list".equals(servletPath)) {
                doList(request,response);
            } else if ("/dept/save".equals(servletPath)) {
                doSave(request,response);
            } else if ("/dept/delete".equals(servletPath)) {
                doDel(request,response);
            } else if ("/dept/edit".equals(servletPath)) {
                doEdit(request,response);
            } else if ("/dept/modify".equals(servletPath)) {
                doModify(request,response);
            } else {
                doDetail(request,response);
            }
    }

    private void doModify(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        //获取提交信息
        String deptno = request.getParameter("deptno");
        String dname = request.getParameter("dname");
        String loc = request.getParameter("loc");

        int count = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "update dept set dname = ?,loc = ? where deptno = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1,dname);
            ps.setString(2,loc);
            ps.setString(3,deptno);
            count = ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(conn,ps,null);
        }
        if(count == 1) {
//            request.getRequestDispatcher("/dept/list").forward(request,response);
            //重定向
            response.sendRedirect(request.getContextPath() + "/dept/list");
        } else {
//            request.getRequestDispatcher("/error.html").forward(request,response);
            response.sendRedirect(request.getContextPath() + "/error.html");
        }
    }

    private void doDel(HttpServletRequest request, HttpServletResponse response)
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
//            request.getRequestDispatcher("/dept/list").forward(request,response);
            response.sendRedirect(request.getContextPath() + "/dept/list");
        } else {
//            request.getRequestDispatcher("/error.html").forward(request,response);
            response.sendRedirect(request.getContextPath() + "/error.html");
        }

    }

    private void doDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

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
        } finally {
            DBUtil.close(conn,ps,rs);
        }
        out.print("<input type='button' value='后退' onclick='window.history.back()'/>");
        out.print("</body>");
        out.print("</html>");

    }

    private void doEdit(HttpServletRequest request, HttpServletResponse response)
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
        } finally {
            DBUtil.close(conn,ps,rs);
        }
        out.print("<input type='submit' value='修改'/><br>");
        out.print("</form>");
        out.print("</body>");
        out.print("</html>");

    }

    private void doSave(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String deptno = request.getParameter("deptno");
        String dname = request.getParameter("dname");
        String loc = request.getParameter("loc");
        Connection conn = null;
        PreparedStatement ps = null;
        int count = 0;
        try {
            conn = DBUtil.getConnection();
            String sql = "insert into dept(deptno,dname,loc) values(?,?,?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1,deptno);
            ps.setString(2,dname);
            ps.setString(3,loc);
            count = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn,ps,null);
        }
        if(count == 1) {
//            request.getRequestDispatcher("/dept/list").forward(request,response);
            //重定向
            response.sendRedirect(request.getContextPath() + "/dept/list");
        } else {
//            request.getRequestDispatcher("/error.html").forward(request,response);
            response.sendRedirect(request.getContextPath() + "/error.html");
        }
    }

    private void doList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String contextPath = request.getContextPath();
        out.print("<!DOCTYPE html>");
        out.print("<html>");
        out.print("<head>");
        out.print("<meta charset='utf-8'>");
        out.print("<title>部门列表页面</title>");

        out.print("<script type='text/javascript'>");
        out.print("function del(dno) {");
        out.print("if (window.confirm('确认删除该部门吗？')) {");
        out.print("document.location.href = '/oa/dept/delete?deptno='+ dno");
        out.print("}");
        out.print("}");
        out.print("</script>");

        out.print("</head>");
        out.print("<body>");
        out.print("<h1 align='center'>部门列表</h1>");
        out.print("<hr>");
        out.print("<table border='1px' align='center' width='50%'>");
        out.print("<tr>");
        out.print("<th>序号</th>");
        out.print("<th>部门编号</th>");
        out.print("<th>部门名称</th>");
        out.print("<th>操作</th>");
        out.print("</tr>");


        //连接数据库，查询所有部门
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil. getConnection();
            String sql = "select deptno as a,dname,loc from dept";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            int i = 0;
            while(rs.next()) {
                String deptno = rs.getString("a");
                String dname = rs.getString("dname");
//                String loc = rs.getString("loc");
                out.print("<tr>");
                out.print("<td>"+(++i)+"</td>");
                out.print("<td>"+deptno+"</td>");
                out.print("<td>"+dname+"</td>");
                out.print("<td>");
                //javascript:void(0)表示不跳转，只执行代码
                out.print("<a href='javascript:void(0)' onclick='del("+deptno+")'>删除</a>");
                out.print("<a href='"+contextPath+"/dept/edit?deptno="+deptno+"'>修改</a>");
                out.print("<a href='"+contextPath+"/dept/detail?deptno="+deptno+"'>详情</a>");
                out.print("</td>");
                out.print("</tr>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn,ps,rs);
        }
        out.print("</table>");
        out.print("<hr>");
        out.print("<a href='"+contextPath+"/add.html'>新增部门</a>");
        out.print("</body>");
        out.print("</html>");
    }

}
