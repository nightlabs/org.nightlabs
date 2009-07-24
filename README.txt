We use the library RXTX for accessing the serial port: http://www.rxtx.org

RXTX is published under the LGPL (GNU Lesser General Public License).

IMPORTANT: You must install RXTX correctly! Even though this project serves for
fulfilling dependencies and allows for compilation against lib/RXTXcomm.jar,
you must follow the installation instructions within
lib/rxtx-2.1-7-bins-r2.zip! Otherwise you cannot access the ports.

On the long run - when we'll include java in the jfire distribution, we will
use this project (JFireRXTX) to deploy the necessary libraries (jars and
DLLs/SOs), but so far, it is solely used for compilation.
