package com.nschirmer.validator

/** Class dedicated to cpf validation
 * The CPF has the ###.###.###-## pattern and it's composed by 11 digits with an specific math to validate the numbers
 * @param cpf can have or not the left overs dots from the CPF mask. It will not matter.
 * **/
class CpfValidation (private var cpf: String) {

    private lateinit var rawCpf: String

    /** Default CPF length **/
    private val cpfLength = 11


    /** Method to cast [Char] to [Int] **/
    private fun Char.getNumericValue(): Int {
        if (!isDigit()) throw NumberFormatException()
        return this.toInt() - '0'.toInt()
    }


    /** When the class is instantiated it will clean the [cpf] input **/
    init { cleanCpfLeftOvers() }


    /** Remove the left over dots from CPF mask if there is any **/
    private fun cleanCpfLeftOvers(){
        rawCpf = cpf.replace(Regex("[.]"),  "")
        rawCpf = rawCpf.replace(Regex("[-]"),  "")
    }


    /** @return If the given CPF is valid, checking special cases and if has all digits **/
    fun isValid(): Boolean {
        return hasAllDigits() && hasValidSymbols() && ! isSpecialCase() &&
                numbersAreValid(false, 2, 0, rawCpf.reversed())
    }


    /** Check if the cpf has all eleven digits **/
    private fun hasAllDigits(): Boolean {
        return rawCpf.length == cpfLength
    }


    /** Check if the cpf is in the ###.###.###-## format **/
    private fun hasValidSymbols(): Boolean {
        return cpf.matches(Regex("^((\\d{3}[.]){2})(\\d{3})([-](\\d{2}))\$"))
    }


    /** Check if the cpf is a special case that can be validated but isn't really valid
     * e.g. 111.111.111-11, 222.222.222-22 ...
     * **/
    private fun isSpecialCase(): Boolean {
        return rawCpf.matches(Regex("^(\\d)\\1*$"))
    }


    /** This method is the heart of the CPF validation. It uses recursion to work.
     * It will go through all numbers of the CPF and then it will check if the validations numbers are valid.
     *
     * Here is how the CPF algorithm works:

     ***********************************************************************
        1th run: We will check if the first validator value is valid
     ***********************************************************************

                                                                (validator digits)
                                                                     v    v
           1    2   3   .    4    5    6    .    7    8    9    -    0    1  > those are the original CPF numbers
           x    x   x        x    x    x         x    x    x
          10    9   8        7    6    5         4    3    2                 > multiply these values with the original CPF numbers
           =    =   =        =    =    =         =    =    =
          10   18   24       28   30   30        28   24   18

          > Them sum all of it
                e.g.  10 + 18 + 24 + 28 + 30 + 30 + 28 + 24 + 18 =  210

          > Find out the mod by 11 form the sum [calculateRemainValueSum]
                e.g. 210 mod 11 = 1

          > If the mod of the division by 11 is less then 2, the value is 0
                e.g. value is 1, so it actually will be 0

            Else if the value is 2 or more the value is 11 - itself
                @example  180 mod 11 = 4   >>>  11 - 4 = 7   >>>>> the value would be 7

          > Now we can check if the value is the same as the first validator digit [getOriginalFirstDigit]
                e.g. Our value after all that is 0 and our fist validator number is 0. For now the CPF is valid.


     ***********************************************************************
        2nd run: We will check if the second validator value is valid
     ***********************************************************************

          > Now comes the second validation. The only difference is the start point of the first multiplication

           1    2   3   .    4    5    6    .    7    8    9    -    0    1  > those are the original CPF numbers
           x    x   x        x    x    x         x    x    x         x
          11    10   9       8    7    6         5    4    3         2        > multiply these values with the original CPF numbers
           =    =   =        =    =    =         =    =    =         =
           11  20   27       32   35  36         35   32   27        0

           > Sum it all and find the mod of 11
                e.g.  11 + 20 + 27 + 24 + 35 + 36 + 35 + 24 + 27 + 0 = 255  >>>>  255 mod 11 = 2

           > Check if the number is less then 2 or not and apply the same math applied on the first run
           > If the mod of the division by 11 is less then 2, the value is 0
                 @example  value is 1, so it actually will be 0

             Else if the value is 2 or more the value is 11 - itself
                 e.g.  The value from the mod was 2, so   >>>>   11 - 2 = 9

           > We can check if the value is the same as the last value from the CPF [getOriginalSecondDigit].
            If is not, the CPF is not valid
                 e.g.  Our value after that is 9 and our second validation digit is 2. The CPF is not valid



     * @param reversedCpf For the recursion to work, it is needed that the CPF is given as reversed order
     *      @example original CPF = 012.345.678-90   reversed CPF = 09-876.543.210
     *
     * @param hasValidatedFirstDigit Is a variable that controls if the first validation digit was checked or not yet
     * @param index Is a control of witch digit to look on the recursion loop and apply the CPF algorithm on to it
     * @param sum It will hold up the sum of the CPF algorithm trough the recursion loop
     * **/
    private fun numbersAreValid(hasValidatedFirstDigit: Boolean, index: Int, sum: Int, reversedCpf: String): Boolean {
        return when (index) {
            cpfLength -> {
                // has reached the end of the cpf string
                val remainValue = calculateRemainValueSum(sum)
                when {
                    hasValidatedFirstDigit -> remainValue == getOriginalSecondDigit()
                    remainValue == getOriginalFirstDigit() -> numbersAreValid(true, 1, 0, reversedCpf)
                    else -> false
                }
            }
            in 0 until cpfLength -> {
                val digitSum = reversedCpf[index].getNumericValue() * (if(hasValidatedFirstDigit) index +1 else index)
                numbersAreValid(hasValidatedFirstDigit, index +1, sum + digitSum, reversedCpf)
            }
            else -> false
        }
    }


    private fun calculateRemainValueSum(sum: Int): Int {
        val remainValue = sum % cpfLength
        return when {
            remainValue > 1 -> cpfLength - remainValue
            else -> 0
        }
    }


    /** @return the first validation digit after the "-" **/
    private fun getOriginalFirstDigit(): Int {
        return rawCpf[rawCpf.lastIndex - 1] .getNumericValue()
    }


    /** @return the second/last validation digit after the "-" **/
    private fun getOriginalSecondDigit(): Int {
        return rawCpf.last().getNumericValue()
    }

}