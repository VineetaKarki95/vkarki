<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<script>
function generate()
{
	   $.ajax({
	        type: 'GENERATE',
	        url: 'OrmController',
	        data: makeJason(),
	        dataType: "text",

	        success: function (response) {
	        },
	        error: function (request, status, error) {

	            alert(error);
	        }
	    });
}
function makeJason()
{
	var buf = "{";
	buf = buf + makeJasonPair("destinationFolder");
	buf = buf + "," + makeJasonPair("templateFolder");
	buf = buf + "," + makeJasonPair("providerType");
	buf = buf + "," + makeJasonPair("dataSource");
	buf = buf + "," + makeJasonPair("userId");
	buf = buf + "," + makeJasonPair("password");
	buf = buf + "," + makeJasonPair("databaseName") + "}";
	return buf;
}
function makeJasonPair(key)
{
	var value = document.getElementById(key).value;
	return " \"" + key + "\" : \"" + value + "\" ";
}
</script>
