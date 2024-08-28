<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="Model.AlbumBean"%>
<%@ page import="Controller.AdminDAO"%>
<%@ page import="Controller.AdminDAO"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Page</title>
    <link rel="shortcut icon" href="img/logonct.png" type="image/x-icon">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="css/admin.css">
    <style>
        body {
            background-color: #FAF4DB;
        }
        .navbar-brand, .nav-link, .btn-outline-warning {
            color: black !important;
        }

        .table img {
            width: 100px;
            height: auto;
        }
        .btn-tambah-buku {
            position: fixed;
            bottom: 40px;
            right: 20px;
        }

        .custom-dropdown-toggle {
            color: white;
            background-color: white;
            border-radius: 5px;
            margin-right: 40px;
            margin-left: 10px;
            padding: 5px 10px;
        }

        .custom-dropdown-toggle:hover {
            color: #ffc107;
            background-color: #495057;
        }

        .custom-dropdown-menu {
            background-color: #343a40;
            border: none;
        }

        .custom-dropdown-item {
            color: #fff;
        }

        .custom-dropdown-item:hover {
            color: #ffc107;
            background-color: #495057;
        }

        .welcome-message {
            display: inline-flex;
            align-items: center;
            padding-left: 20px;
            margin-left: auto;
        }

        .welcome-text {
            color: white;
            font-size: 30px;
            font-weight: bold;
            margin-left: 30px;
        }

        .username-text {
            color: white;
            font-weight: bold;
            margin-left: 8px;
            font-size: 20px;
        }

        .header {
            background-color: black;
            padding: 20px;
            color: white;
            text-align: center;
        }

        .menu {
            position: absolute;
            top: 20px;
            right: 20px;
            color: white;
        }

        .content {
            margin: 20px;
        }

        .add-album-btn {
            background-color: #b4a9a2;
            color: white;
            font-weight: bold;
            border: none;
            padding: 10px 20px;
            margin: 20px 0;
            cursor: pointer;
            border-radius: 15px;
        }

        .add-album-btn:hover {
            background-color: #5e524e;
        }

        .table th, .table td {
            vertical-align: middle;
            text-align: center;
        }

        .table th {
            background-color: black;
            color: white;
        }

        .action-btn {
            margin: 5px;
        }

        .table-bordered {
            border: 2px solid black;
        }

        .table-bordered th,
        .table-bordered td {
            border: 2px solid black;
        }

        /* Modal Styles */
        .modal-content {
            border-radius: 10px;
            padding: 20px;
            background-color: #fffbe6;
            border: 2px solid #6d6966;
        }

        .modal-header {
            border-bottom: none;
        }

        .modal-title {
            color: #6d6966;
            font-weight: bold;
        }

        .close {
            font-size: 1.5rem;
            color: #6d6966;
        }

        .form-group label {
            font-weight: bold;
            color: #6d6966;
        }

        .form-control, .form-control-file {
            border: 2px solid #6d6966;
            border-radius: 10px;
            padding: 10px;
            background-color: #fffbe6;
        }

        textarea.form-control {
            resize: none;
        }

        .btn-primary {
            background-color: #6d6966;
            border: none;
            border-radius: 20px;
            padding: 10px 20px;
            font-weight: bold;
            color: white;
            cursor: pointer;
        }

        .btn-primary:hover {
            background-color: #5f574d;
        }

        .btn-primary:focus {
            outline: none;
            box-shadow: none;
        }

        .form-group select {
            width: 100%;
        }
    </style>
</head>
<body>
    <%
        AdminDAO dao = new AdminDAO();
        List<AlbumBean> albums = dao.getAllalbums();

        String username = (String) session.getAttribute("uName");
        boolean isLoggedIn = (username != null);
    %>

    <nav class="navbar navbar-expand-lg navbar-dark" style="background-color: black;">
        <% if (isLoggedIn) { %>
            <span class="navbar-text mr-3">
                <span class="welcome-text">Welcome</span>
                <span class="username-text"><%= username %></span>
            </span>
        <% } %>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavDropdown"
            aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNavDropdown">
            <ul class="navbar-nav ml-auto">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle custom-dropdown-toggle" href="#" id="navbarDropdownMenuLink" role="button"
                        data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        Menu
                    </a>
                    <div class="dropdown-menu dropdown-menu-right custom-dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
                      <a class="dropdown-item custom-dropdown-item" href="index.jsp?user=admin">Home User</a>

                        <a class="dropdown-item custom-dropdown-item" href="admin.jsp">Home Admin</a>
                        <a class="dropdown-item custom-dropdown-item" href="AdminTransactionServlet">Order</a>
                        <a class="dropdown-item custom-dropdown-item" href="SignOutServlet">SignOut</a>
                    </div>
                </li>
            </ul>
        </div>
    </nav>

    <div class="content">
        <button class="add-album-btn" data-toggle="modal" data-target="#tambahBukuModal">Add Album +</button>
        <table class="table table-bordered">
            <thead>
                <tr>
                    <th>Album Image</th>
                    <th>Album Name</th>
                    <th>Album Price</th>
                    <th>Unit</th>
                    <th>Sub Unit</th>
                    <th>Album Description</th>
                    <th>Stock</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <% for (AlbumBean album : albums) { %>
                    <tr>
                        <td>
                            <img src="<%= request.getContextPath() %>/ImageServlet?id=<%= album.getId() %>" alt="album Image">
                        </td>
                        <td><%= album.getName() %></td>
                        <td><%= album.getPrice() %></td>
                        <td><%= album.getUnit() %></td>
                       <td><%=  AdminDAO.formatSubUnit(album.getSubUnit()) %></td>
                        <td><%= album.getDescription() %></td>
                        <td><%= album.getStock() %></td>
                        <td>
                            <form action="adminServlet" method="post" style="display: inline-block;">
                                <input type="hidden" name="action" value="edit">
                                <input type="hidden" name="albumId" value="<%= album.getId() %>">
                                <button type="submit" class="btn btn-warning action-btn">Update</button>
                            </form>
                            <form action="adminServlet" method="post" style="display: inline-block;">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="albumId" value="<%= album.getId() %>">
                                <button type="submit" class="btn btn-danger action-btn">Delete</button>
                            </form>
                        </td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>

    <!-- Modal -->
    <div class="modal fade" id="tambahBukuModal" tabindex="-1" role="dialog" aria-labelledby="tambahBukuModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="tambahBukuModalLabel">Add Album</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form action="adminServlet" method="post" enctype="multipart/form-data">
                        <input type="hidden" name="action" value="add">
                        <div class="form-group">
                            <label for="albumImage">Album Image</label>
                            <input type="file" class="form-control-file" id="albumImage" name="albumImage" accept="image/*" required>
                        </div>
                        <div class="form-group">
                            <label for="albumName">Album Name</label>
                            <input type="text" class="form-control" id="albumName" name="album_name" required>
                        </div>
                        <div class="form-group">
                            <label for="albumPrice">Album Price</label>
                            <input type="number" class="form-control" id="albumPrice" name="album_price" required>
                        </div>
                        <div class="form-group">
                            <label for="unit">Unit</label>
                            <select class="form-control" id="unit" name="unit" required>
                                <option value="">Choose Unit</option>
                                <option value="NCT">NCT</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="subUnit">Sub Unit</label>
                            <select class="form-control" id="subUnit" name="subUnit" required>
                                <option value="">Choose SubUnit</option>
                                <option value="NCT 127">NCT 127</option>
                                <option value="NCT DREAM">NCT DREAM</option>
                                <option value="Way V">Way V</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="descriptionAlbum">Album Description</label>
                            <textarea class="form-control" id="descriptionAlbum" name="album_description" rows="3" required></textarea>
                        </div>
                        <div class="form-group">
                            <label for="stock">Stock</label>
                            <input type="number" class="form-control" id="stock" name="stock" required>
                        </div>
                        <button type="submit" class="btn btn-primary">Add Album</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.11.0/umd/popper.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>
</body>
</html>