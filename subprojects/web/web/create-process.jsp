<%@include file="inc/header.jsp" %>
<h2>Create a Process</h2>
<form action="create-process.jsp" enctype="multipart/form-data" method="post">
    <div class="item">
        <div class="name">Name</div>
        <div class="input">
            <input name="name" type="text"/>
        </div>
    </div>
    <div class="item">
        <div class="name">Process</div>
        <div class="input">
            <input name="process" type="text"/>
        </div>
    </div>
    <div class="item">
        <div class="name">Inputs</div>
        <div class="input">
            <input name="input-0" type="file"/>
        </div>
    </div>
</form>
<%@include file="inc/footer.jsp" %>