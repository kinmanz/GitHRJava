/**
 * Created by kinmanz on 26.03.17.
 */


$(document).ready( function() {
    $.get('/api/session/has_token', {}, function(data){
        if (data == "true" )
        {
            $('#intro_signin').hide();
            $("#intro_search").show();

            $("#top-user-info").show();
        }
    });

    $(document).on('click', '#intro_search_button', function() {
        var login = $("#intro_search_input").val();
        window.location.href = "/" + login;
    });
});