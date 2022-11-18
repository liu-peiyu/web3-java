package info.example.web3j.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@RestController
@RequestMapping
public class IndexController {

    private static String address1 = "0x92550C6C5ca3CfE8E8d350b83a3aFAd5466ADc8a";
    private static String privateKey1 = "89d97bbdd1c84dad5ee73795adcaec28cefa37f6c00b32ebacd60b7b5af3b158";
    private static String walletName1 = "/UTC--2022-11-09T06-27-10.928000000Z--92550c6c5ca3cfe8e8d350b83a3afad5466adc8a.json";

    private static String address2 = "0x24C0F3C6c3fDA8C5Ad6CFD3ab278Ef9C9d58adF9";
    private static String privateKey2 = "84a22d3bdb8a7a6fb2bf93ac4a0d796ecaf5475f6439d6750315f471268babc4";
    private static String walletName2 = "/UTC--2022-11-09T06-27-44.241000000Z--24c0f3c6c3fda8c5ad6cfd3ab278ef9c9d58adf9.json";

    private static String password = "123456";

    private static String walletPath = "e:/wallet";

    @Autowired
    private Web3j web3j;

    @GetMapping("")
    public String index() {
        return "Hello Wrold";
    }

    /**
     *
     * @return
     */
    @GetMapping("/getVersion")
    public String getVersion() {
        String version = "";
        try {
            version = web3j.web3ClientVersion().send().getWeb3ClientVersion();
        } catch (IOException e) {
            e.printStackTrace();
            version = e.getMessage();
        }
        return version;
    }

    @GetMapping("/getBalance")
    public BigDecimal getBalance(){
        BigDecimal balance = BigDecimal.ZERO;
        BigInteger ethBalance = BigInteger.ZERO;
        try {
            EthGetBalance ethGetBalance = web3j.ethGetBalance(address1, DefaultBlockParameter.valueOf("latest")).send();
            ethBalance = ethGetBalance.getBalance();
            System.out.println(ethBalance.toString());
            balance = Convert.fromWei(ethBalance.toString(), Convert.Unit.ETHER);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return balance;
    }

    /**
     * 创建钱包
     * @return
     * @throws InvalidAlgorithmParameterException
     * @throws CipherException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws NoSuchProviderException
     */
    @GetMapping("/generateWallet")
    public String generateWallet() throws InvalidAlgorithmParameterException, CipherException, NoSuchAlgorithmException, IOException, NoSuchProviderException {
        String walletFileName = "";
//        walletFileName = WalletUtils.generateNewWalletFile("123456", new File(walletFlePath), false);
        ECKeyPair ecKeyPair = ECKeyPair.create(new BigInteger(privateKey2, 16));
        walletFileName = WalletUtils.generateWalletFile("123456", ecKeyPair, new File(walletPath), false);
        return walletFileName;
    }

    private Credentials loadCredentials() throws CipherException, IOException {
        Credentials credentials = WalletUtils.loadCredentials(password, new File(walletPath + walletName1));
        return credentials;
    }

    @GetMapping("/transfer")
    public String createTransfer() throws Exception {
        Credentials credentials = loadCredentials();
        TransactionReceipt receipt = Transfer.sendFunds(web3j, credentials, address2, BigDecimal.valueOf(1), Convert.Unit.ETHER).send();
        return "getTransactionHash: "+ receipt.getTransactionHash() +" getBlockHash："+ receipt.getBlockHash() +"  gas=" + receipt.getGasUsed()+" status="+ receipt.getStatus();
    }

    @GetMapping("/test")
    public String test(){
        Boolean iz = WalletUtils.isValidPrivateKey("89d97bbdd1c84dad5ee73795adcaec28cefa37f6c00b32ebacd60b7b5af3b158");
        System.out.println(iz);
        return "";
    }




}
