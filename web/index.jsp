<%@ page import="Controller.AlbumDAO, Model.AlbumBean, java.sql.Connection, java.util.List" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<%@ page import="DB.DBConnection" %>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%
    DBConnection dbInstance = new DBConnection();
    Connection connection = dbInstance.getConnection();
    AlbumDAO albumDAO = new AlbumDAO();
    List<AlbumBean> albums = albumDAO.getAllAlbums();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/style.css">
    <link rel="shortcut icon" href="img/logonct.png" type="image/x-icon">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:ital,wght@0,100..900;1,100..900&display=swap" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script>
        var counter = 1;
        setInterval(() => {
            document.getElementById('radio' + counter).checked = true;
            counter++;
            if(counter > 3){
                counter = 1;
            }
        }, 30000); // Ubah gambar setiap 30 detik

        $(document).ready(function(){
            var showCartNotification = '<%= session.getAttribute("showCartNotification") %>';
            if (showCartNotification === 'true') {
                $('#cartModal').modal('show');
                <% session.setAttribute("showCartNotification", false); %>
            }
        });

        function openModal(event, id, title, price, unit, synopsis, imgSrc, stock) {
            event.preventDefault();
            if (stock <= 0) return; // Prevent modal for out of stock album

            const modal = document.getElementById('modal');
            document.getElementById('modal-title').innerText = title;
            document.getElementById('modal-price').innerText = 'Rp ' + price;
            document.getElementById('modal-unit').innerText = unit;
            document.getElementById('modal-synopsis').innerText = synopsis;
            document.getElementById('modal-img').src = imgSrc;
            document.getElementById('modal-album-id').value = id;
            document.getElementById('modal-album-name').value = title;
            document.getElementById('modal-album-price').value = price;
            document.getElementById('modal-stock').innerText = 'Stock buku: ' + stock + ' item';
            if (stock > 0) {
                document.getElementById('modal-add-to-cart').disabled = false;
            } else {
                document.getElementById('modal-add-to-cart').disabled = true;
            }
            modal.style.display = "block";
        }

        function closeModal() {
            document.getElementById('modal').style.display = "none";
        }
    </script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Zen+Maru+Gothic&display=swap');
        body, html {
            font-family: 'Zen Maru Gothic', sans-serif;
            margin: 0;
            padding: 0;
            background-color: #FFFFEF;
        }

        #modal-img {
            width: 180px;
        }
        
        .out-of-stock {
            background-color: gray;
            pointer-events: none;
            cursor: not-allowed;
            position: relative;
            opacity: 0.6;
        }
        
        .out-of-stock::after {
            content: "SOLD OUT";
            color: red;
            font-size: 20px;
            font-weight: bold;
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background-color: rgba(255, 255, 255, 0.8);
            padding: 5px;
        }
        
        nav {
            margin-bottom: 0;
        }

        .slider {
            width: 100%;
            height: 100vh;
            overflow: hidden;
            position: relative;
        }

        .slides {
            display: flex;
            width: 300%;
            height: 100%;
        }

        .slides input {
            display: none;
        }

        .slide {
            width: 33.3333%;
            transition: margin-left 0.7s;
            height: 100%;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .slide img {
            padding: 20px 0;
            width: 100%;
            height: 100%;
            object-fit: cover;
            border-radius: 0;
        }

        .navigation-auto {
            position: absolute;
            width: 100%;
            margin-top: -40px;
            display: flex;
            justify-content: center;
        }

        .auto-btn1, .auto-btn2, .auto-btn3 {
            border: 2px solid #fff;
            padding: 5px;
            border-radius: 50%;
            cursor: pointer;
            transition: 0.4s;
            background-color: rgba(0, 0, 0, 0.5);
        }

        .auto-btn1:hover, .auto-btn2:hover, .auto-btn3:hover {
            background: #fff;
        }

        #radio1:checked ~ .first {
            margin-left: 0;
        }

        #radio2:checked ~ .first {
            margin-left: -33.3333%;
        }

        #radio3:checked ~ .first {
            margin-left: -66.6667%;
        }

        .manual-btn {
            border: 2px solid #fff;
            padding: 5px;
            border-radius: 50%;
            cursor: pointer;
            transition: 0.4s;
            display: inline-block;
            background-color: rgba(0, 0, 0, 0.5);
        }

        .manual-btn:hover {
            background: #fff;
        }

        .navigation-manual {
            position: absolute;
            bottom: 20px;
            width: 100%;
            display: flex;
            justify-content: center;
            z-index: 1000;
        }

        .unit-icon {
            width: 100%;
            padding: 20px 0;
            background-color: #FFFFEF;
        }

        .row {
            display: flex;
            flex-wrap: wrap;
            justify-content: center;
        }

        .column {
            flex: 0 0 30%;
            max-width: 30%;
            margin: 10px;
            text-align: center;
        }

        .icons {
            margin: 10px;
            text-align: center;
            color: black;
            text-decoration: none;
        }

        .icons img {
            width: 100%;
            height: auto;
        }

        .icons:hover {
            background-color: #FFF;
            color: #000;
        }

        .carousel-main-div1 {
            background-color: #FFFFEF;
            padding: 20px;
            margin-bottom: 20px;
        }

        .carousel-main-div1 img {
            height: 60%;
        }

        .price {
            font-size: 30%;
        }

        .carousel-main-div1 h3 {
            height: 15%;
        }

        .prev-main, .next-main {
            background-color: #555;
            color: white;
            border: none;
            cursor: pointer;
            padding: 10px 15px;
            font-size: 18px;
            border-radius: 5px;
            transition: background-color 0.3s ease;
        }

        .prev-main:hover, .next-main:hover {
            background-color: #333;
        }

        .carousel-main {
            position: relative;
        }

        .card-best, .card-new {
            min-width: 200px;
            margin: 10px;
            padding: 10px;
            border-radius: 8px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
            background-color: #FFFFEF;
            text-align: center;
            cursor: pointer;
            transition: transform 0.3s ease;
            position: relative;
        }

        .icons h3 {
            color: black;
            text-decoration: none;
            cursor: pointer;
        }

        .icons h3:hover {
            text-decoration: none;
        }
    </style>
    <title>NCT AlbumStore</title>
</head>
<body>
    <%@ include file="header.jsp" %>
    <!-- Slideshow container -->
    <div class="slider">
        <div class="slides">
            <input type="radio" name="radio-btn" id="radio1" checked>
            <input type="radio" name="radio-btn" id="radio2">
            <input type="radio" name="radio-btn" id="radio3">

            <div class="slide first">
                <img src="https://upload.wikimedia.org/wikipedia/commons/1/12/NCT_127_logo.png?20190108023940" alt="Slide 1">
            </div>

            <div class="slide">
                <img src="https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcSmhYvKIqF6XhKRf7RhDe_YAPFe5w8QPGxt3R7HPJtE-xB0dhaq2gl9ddZU5fYi" alt="Slide 2">
            </div>
            
            <div class="slide">
                <img src="https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcTu2qBFcjeILtBQKtIohzckdA4ty7tFVZs0kJtR6VdjjmozCJzffP42rt69h3a8" alt="Slide 3">
            </div>

            
            <div class="navigation-auto">
                <div class="auto-btn1"></div>
                <div class="auto-btn2"></div>
                <div class="auto-btn3"></div>
            </div>
        </div>

        <div class="navigation-manual">
            <label for="radio1" class="manual-btn"></label>
            <label for="radio2" class="manual-btn"></label>
            <label for="radio3" class="manual-btn"></label>
        </div>
    </div>

    <!-- Section unit-icon -->
    <section class="unit-icon">
        <a href="nct127.jsp">
            <div class="icons">
                <img src="img/NCT 127/nct127.jpeg" alt="album Icon">
                <div>
                    <h3>NCT 127</h3>
                </div>
            </div>
        </a>
        
        <a href="nctDream.jsp">
            <div class="icons">
                <img src="img/NCT Dream/nctdream.jpeg" alt="album Icon">
                <div>
                    <h3>NCT Dream</h3>
                </div>
            </div>
        </a>
        <a href="wayV.jsp">
            <div class="icons">
                <img src="img/WayV/wayV.jpeg" alt="album Icon">
                <div>
                    <h3>Way V</h3>
                </div>
            </div>
        </a>
        
    </section>

    <div id="modal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeModal()">&times;</span>
            <img id="modal-img" src="" alt="album Image">
            <h2 id="modal-title">album Title</h2>
            <p id="modal-author">Author</p>
            <p id="modal-price">Price</p>
            <p id="modal-unit">Unit</p>
            <p id="modal-synopsis">Synopsis</p>
            <p id="modal-stock">Stock Album: </p>
            <form id="modal-form" action="<%=request.getContextPath()%>/AddToCartServlet" method="post">
                <input type="hidden" id="modal-album-id" name="albumId">
                <input type="hidden" id="modal-album-name" name="albumName">
                <input type="hidden" id="modal-album-price" name="albumPrice">
                <button type="submit" id="modal-add-to-cart">Add to Cart</button>
            </form>
        </div>
    </div>

    <%@ include file="footer.jsp" %>

    <script type="text/javascript" src="javascript/script.js"></script>
</body>
</html>
