## [ZECmate Swing Wallet](https://swing.zecmate.com/)

## ZECmate Swing Wallet is distributed via [APT Repository](https://zecmate.github.io/aptrepo/) or can be downloaded directly from [Releases](https://github.com/ZECmate/ZECmate-swing-wallet/releases) where signed .deb package is distributed or from official [ZECmate Swing Wallet Website](https://swing.zecmate.com)

![Screenshot](zecmate.png "Main Window")

**This wallet is targeted at advanced users who understand the implications of running a full Zcash node on**
**the local machine, maintaining a full local copy of the blockchain, maintaining and backing up the**
**Zcash nodes's `wallet.dat` file etc! The wallet is not suitable for novice crypto-currency users!**


### Installing the ZECmate Swing Wallet on Linux
*Zcash is needed to be installed, follow https://z.cash/download.html prior to installing this ZECmate Swing wallet.*

To setup the APT repository and install packages, using a terminal run the following commands 
```
echo 'deb https://zecmate.github.io/aptrepo/ all main' | sudo tee --append /etc/apt/sources.list.d/zecmate.list
gpg --keyserver keyserver.ubuntu.com --recv 4B69CA27A986265D
gpg --export 4B69CA27A986265D | sudo apt-key add -

sudo apt-get update
sudo apt-get install zecmate
```
   
### Running the ZECmate Swing Wallet on Linux

To launch the ZECmate Swing Wallet you can just search and click on it in the Ubuntu unity menu or alternatively, run the command `zecmate` from a terminal:
```
zecmate
```

### Disclaimer

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

### Known issues and limitations
1. Limitation: if two users exchange text messages via the messaging UI TAB and one of them has a system clock, substantially running slow or fast by more than 1 minute, it is possible that this user will see text messages appearing out of order. 
1. Limitation: if a messaging identity has been created (happens on first click on the messaging UI tab), then replacing the `wallet.dat` or changing the node configuration between mainnet and testnet will make the identity invalid. This will result in a wallet update error. To remove the error the directory `~/.ZECmate/messaging` may be manually renamed or deleted (when the wallet is stopped). **CAUTION: all messaging history will be lost in this case!**
1. Limitation: The list of transactions may not show all outgoing ones (specifically outgoing Z address 
transactions). A corresponding issue [#1438](https://github.com/zcash/zcash/issues/1438) has been opened 
for the Zcash developers. 
1. Limitation: The CPU percentage shown to be taken by zcashd on Linux is the average for the entire lifetime 
of the process. This is not very useful. This will be improved in future versions.
