package com.lemon.interview;

/**
 * 腾讯面试题，有50个台阶，一次走一步或者两步，有多少种可能
 * 
 *


	台阶数	可能数	备注
	1		1
	2		2
	3		3 		1+1+1 2+1 1+2
	4       5       1+1+1+1 1+1+2 1+2+1 2+1+1 2+2
	5       8
 * 
 * 当N>2时，可能数=(N-1)步的可能+(N-2)步的可能
 * @author andy
 *
 */
public class QQInterview {
	
	public static void main(String[] args) {
		//System.out.println(test(50));//20365011074
		System.out.println(test(12));//233
		System.out.println(test2(12));
	}
	
	static long test2(int n){
		long total=0;
		long step1=1;
		long step2=2;
		//当N>2时，可能数=(N-1)步的可能+(N-2)步的可能
		for(int i=3;i<=n;i++){
			total=step1+step2;
			step1=step2;
			step2=total;
		}
		
		return total;
	}
	
	static long test(int n){
		if(n==1){
			return 1;
		}else if(n==2){
			return 2;
		}else{
			return test(n-1)+test(n-2);
		}
		
	}
	
}
