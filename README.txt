WELCOME
--------
Thank you for using the FrontlineSMS Sync plugin.


ABOUT Sync Plugin 0.0.1
-----------------
This plugin is for pushing text messages received on FrontlineSMS to a website (e.g. an instance of the Ushahidi platform). The plugin
allows you to specify the parameters names & values to be submitted to the target resource. Most target URLs for receiving SMS take on the
following form: http://example.com/?key={sms_key}&sender=${sender_number}&message=${message_content}

Sync ALWAYS retries sending messages that have not been pushed the target URL due to lack of Internet connectivity. This has the 
following advantages:
 * It provides  a way of queueing up messages as they are received on FrontlineSMS
 * Queued messages are automatically pushed to the target URL once Internet connectivity is restored

AUTHORS
------
Emmanuel Kala <emmanuel(at)ushahidi.com>
Alex Anderson <alex(at)frontlinesms.com>
