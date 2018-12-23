package com.eventino.web.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eventino.db.connection.DBConnection;
import com.eventino.web.model.EventModel;

/**
 * Servlet implementation class Event
 */
@WebServlet("/Event")
public class Event extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Event() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);

		String eventID = request.getParameter("eventID");
		
		System.out.println(eventID);

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DBConnection.createConnection();
			Statement stmt = conn.createStatement();

			ResultSet event = stmt.executeQuery("SELECT * FROM event WHERE event_id='"+ eventID + "'");
			if(event.next()) {
				EventModel eventInfo = new EventModel(event.getInt("event_id"),
						event.getInt("advertiser_id"),
						event.getInt("system_account_transaction_id"),
						event.getInt("account_transaction_id"),
						event.getString("event_address"),
						event.getString("event_title"),
						event.getString("event_desc"),
						event.getDate("event_expire_date"),
						event.getDate("event_publish_date"),
						event.getDate("event_time"),
						event.getString("event_type"),
						event.getString("event_photo"));
				request.setAttribute("event", eventInfo);
			}else {
				RequestDispatcher reqDispatcher = getServletConfig().getServletContext()
						.getRequestDispatcher("/events.jsp");
				reqDispatcher.forward(request, response);
			}

			event.close();

			ResultSet ticketSP = stmt.executeQuery("SELECT ticket.price FROM ticket,event WHERE event.event_id = '" + eventID + "' AND event.event_id = ticket.event_id AND ticket.ticket_type = 'Student' ORDER BY ticket.ticket_id LIMIT 1");
			if(ticketSP.next()) {
				request.setAttribute("ticketSP", ticketSP.getInt("price"));
			}else {
				RequestDispatcher reqDispatcher = getServletConfig().getServletContext()
						.getRequestDispatcher("/events.jsp");
				reqDispatcher.forward(request, response);
			}

			ticketSP.close();


			ResultSet ticketNSP = stmt.executeQuery("SELECT ticket.price FROM ticket,event WHERE event.event_id = '" + eventID + "' AND event.event_id = ticket.event_id AND ticket.ticket_type = 'Non-Student' ORDER BY ticket.ticket_id LIMIT 1");
			if(ticketNSP.next()) {
				request.setAttribute("ticketNSP", ticketNSP.getInt("price"));

			}else {
				RequestDispatcher reqDispatcher = getServletConfig().getServletContext()
						.getRequestDispatcher("/events.jsp");
				reqDispatcher.forward(request, response);
			}

			ticketNSP.close();

			RequestDispatcher reqDispatcher = getServletConfig().getServletContext()
					.getRequestDispatcher("/event.jsp");
			reqDispatcher.forward(request, response);



		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			RequestDispatcher reqDispatcher = getServletConfig().getServletContext()
					.getRequestDispatcher("/index.jsp");
			reqDispatcher.forward(request, response);
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			RequestDispatcher reqDispatcher = getServletConfig().getServletContext()
					.getRequestDispatcher("/index.jsp");
			reqDispatcher.forward(request, response);
			e.printStackTrace();
		}
	}

}
